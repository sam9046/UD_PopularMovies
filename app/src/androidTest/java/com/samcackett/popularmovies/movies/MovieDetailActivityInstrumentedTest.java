package com.samcackett.popularmovies.movies;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.app.ActionBar;

import com.samcackett.popularmovies.R;
import com.samcackett.popularmovies.model.tmdb.MovieModel;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

/**
 * Created by sam on 21/08/2017.
 */
@RunWith(AndroidJUnit4.class)
public class MovieDetailActivityInstrumentedTest {
    @Rule
    final public ActivityTestRule<MovieDetailActivity> movieDetailActivityTestRule = new ActivityTestRule<>(MovieDetailActivity.class);

    @Test
    public void startActivity_loadMovieDetailWithNoData() throws Exception {
        Intent result = getIntent();
        movieDetailActivityTestRule.launchActivity(result);
        onView(withId(R.id.movie_detail_card_view)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.movie_detail_error_text_view)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void startActivity_loadMovieDetailWithData() throws Exception {
        MovieModel movieModel = generateMovieModel();
        Intent result = getIntent();
        result.putExtra("MovieModel", movieModel);
        movieDetailActivityTestRule.launchActivity(result);

        onView(withId(R.id.movie_detail_card_view)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.movie_detail_error_text_view)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        checkReleaseDateMatches(movieModel);
        checkUserRatingMatches(movieModel);
        checkTitleMatches(movieModel);
        checkSynopsisMatches(movieModel);
    }

    private void checkReleaseDateMatches(MovieModel movieModel) {
        onView(withId(R.id.card_release_date)).check(matches(withText(movieModel.getReleaseDate())));
    }

    private void checkUserRatingMatches(MovieModel movieModel) {
        String parsedRating = movieDetailActivityTestRule.getActivity().parseRating(movieModel.getVoteAverage().toString());
        onView(withId(R.id.card_user_rating)).check(matches(withText(parsedRating)));
    }

    private void checkTitleMatches(MovieModel movieModel) {
        String expectedTitle = movieModel.getOriginalTitle();
        ActionBar actionBar = movieDetailActivityTestRule.getActivity().getSupportActionBar();
        String actualTitle = "";
        if(actionBar != null) {
            CharSequence charSequence = actionBar.getTitle();
            if(charSequence != null && charSequence.length() > 0) {
                actualTitle = actionBar.getTitle().toString();
            }
        }
        assertEquals(expectedTitle, actualTitle);
    }

    private void checkSynopsisMatches(MovieModel movieModel) {
        onView(withId(R.id.card_synopsis)).check(matches(withText(movieModel.getOverview())));
    }

    @Test
    public void parseRating_shouldReturnRatingAppendedWithSlashTen() throws Exception {
        String rating = "1.0";
        String expectedRating = "1.0/10";
        String actualRating = movieDetailActivityTestRule.getActivity().parseRating(rating);
        assertEquals(expectedRating, actualRating);
    }

    @NonNull
    private Intent getIntent() {
        Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        return new Intent(targetContext, MovieDetailActivity.class);
    }

    private MovieModel generateMovieModel() {
        return new MovieModel(
                4246,
                321612,
                false,
                6.8,
                "Beauty and the Beast",
                131.6189,
                "/tWqifoYuwLETmmasnGHO7xBjEtt.jpg",
                "en",
                "Beauty and the Beast",
                new ArrayList<>(Arrays.asList(10751, 14, 10749)),
                "/6aUWe0GSl69wMTSWWexsorMIvwU.jpg",
                false,
                "A live-action adaptation of Disney's version of the classic 'Beauty and the Beast' tale of a cursed prince and a beautiful young woman who helps him break the spell.",
                "2017-03-16"
        );
    }


}