package com.samcackett.popularmovies.movies;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.samcackett.popularmovies.R;
import com.samcackett.popularmovies.model.tmdb.MovieModel;
import com.samcackett.popularmovies.util.URLBuilder;
import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {

    private static final String MOVIE_MODEL = "MovieModel";
    private ImageView imageView;
    private ImageView cardImageView;
    private TextView releaseDateView;
    private TextView synopsisView;
    private TextView cardRatingView;
    private CardView cardView;
    private TextView errorTextView;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(0);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        imageView = (ImageView) findViewById(R.id.movie_bg_img_view);
        cardImageView = (ImageView) findViewById(R.id.card_thumbnail_image_view);
        releaseDateView = (TextView) findViewById(R.id.card_release_date);
        synopsisView = (TextView) findViewById(R.id.card_synopsis);
        cardRatingView = (TextView) findViewById(R.id.card_user_rating);
        cardView = (CardView) findViewById(R.id.movie_detail_card_view);
        errorTextView = (TextView) findViewById(R.id.movie_detail_error_text_view);
        Intent intent = getIntent();
        if(intent != null) {
            loadUI(intent);
        }
    }

    private void loadUI(Intent intent) {
        MovieModel movieModel = intent.getParcelableExtra(MOVIE_MODEL);
        if(movieModel != null) {
            showGenericError(false);
            Picasso.with(MovieDetailActivity.this)
                    .load(URLBuilder.createBackgroundUrl(movieModel.getBackdropPath()))
                    .error(R.drawable.error_cloud_icon)
                    .into(imageView);
            actionBar.setTitle(movieModel.getOriginalTitle());
            Picasso.with(MovieDetailActivity.this)
                    .load(URLBuilder.createImageUrl(movieModel.getPosterPath()))
                    .error(R.drawable.error_cloud_icon)
                    .into(cardImageView);
            releaseDateView.setText(movieModel.getReleaseDate());
            synopsisView.setText(movieModel.getOverview());
            String rating = parseRating(movieModel.getVoteAverage().toString());
            cardRatingView.setText(rating);
        } else {
            showGenericError(true);
        }
    }

    public String parseRating(String rating) {
        return rating + "/10";
    }

    private void showGenericError(boolean show) {
        cardView.setVisibility(show ? View.GONE : View.VISIBLE);
        errorTextView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
