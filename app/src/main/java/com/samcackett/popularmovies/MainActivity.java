package com.samcackett.popularmovies;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.samcackett.popularmovies.api.TmdbService;
import com.samcackett.popularmovies.model.tmdb.MovieModel;
import com.samcackett.popularmovies.model.tmdb.TmdbResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String TMDB_RESPONSE = "TMDB_RESPONSE";
    private RecyclerViewAdapter adapter;
    private ProgressBar progressBar;
    private TextView errorTextView;
    private boolean topRatedLoaded;
    private boolean mostPopularLoaded;
    private TmdbResponse tmdbResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Pop Movies");
        setSupportActionBar(toolbar);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        errorTextView = (TextView) findViewById(R.id.error_text_view);

        // Set up recycler view
        RecyclerView.LayoutManager layoutManager;
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager = new GridLayoutManager(MainActivity.this, 4);
        } else {
            layoutManager = new GridLayoutManager(MainActivity.this, 2);
        }
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerViewAdapter(MainActivity.this);
        recyclerView.setAdapter(adapter);

        if(savedInstanceState != null
                && savedInstanceState.getString(TMDB_RESPONSE) != null) {
            tmdbResponse = new Gson().fromJson(savedInstanceState.getString(TMDB_RESPONSE), TmdbResponse.class);
            updateRecyclerView(tmdbResponse.getMovieModels());
        } else {
            fetchMostPopularMovies();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        String jsonString = new Gson().toJson(tmdbResponse);
        outState.putString(TMDB_RESPONSE, jsonString);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        String jsonString = new Gson().toJson(tmdbResponse);
        outState.putString(TMDB_RESPONSE, jsonString);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_by_popular:
                fetchMostPopularMovies();
                return true;
            case R.id.sort_by_highest_rated:
                fetchTopRatedMovies();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void fetchMostPopularMovies() {
        if(!mostPopularLoaded) {
            topRatedLoaded = false;
            clearRecyclerView();
            showProgressBar(true);
            TmdbService.Service service = TmdbService.getInstance();
            Call<TmdbResponse> call = service.getPopularMovies();
            call.enqueue(new Callback<TmdbResponse>() {
                @Override
                public void onResponse(@NonNull Call<TmdbResponse> call, @NonNull Response<TmdbResponse> response) {
                    mostPopularLoaded = true;
                    showProgressBar(false);
                    tmdbResponse = response.body();

                    if(tmdbResponse != null) {
                        updateRecyclerView(tmdbResponse.getMovieModels());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<TmdbResponse> call, @NonNull Throwable t) {
                    mostPopularLoaded = false;
                    showProgressBar(false);
                    Log.e(TAG, t.getMessage());
                    showGenericError();
                }
            });
        }
    }

    private void clearRecyclerView() {
        updateRecyclerView(new ArrayList<MovieModel>());
    }

    private void fetchTopRatedMovies() {
        if(!topRatedLoaded) {
            mostPopularLoaded = false;
            clearRecyclerView();
            showProgressBar(true);
            TmdbService.Service service = TmdbService.getInstance();
            Call<TmdbResponse> call = service.getTopRatedMovies();
            call.enqueue(new Callback<TmdbResponse>() {
                @Override
                public void onResponse(@NonNull Call<TmdbResponse> call, @NonNull Response<TmdbResponse> response) {
                    topRatedLoaded = true;
                    showProgressBar(false);
                    tmdbResponse = response.body();

                    if(tmdbResponse != null) {
                        updateRecyclerView(tmdbResponse.getMovieModels());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<TmdbResponse> call, @NonNull Throwable t) {
                    topRatedLoaded = false;
                    showProgressBar(false);
                    Log.e(TAG, t.getMessage());
                    showGenericError();
                }
            });
        }
    }

    private void showGenericError() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                errorTextView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showProgressBar(final boolean show) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(progressBar != null) {
                    progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
                if(errorTextView != null && show) {
                    errorTextView.setVisibility(View.GONE);
                }
            }
        });
    }

    private void updateRecyclerView(final List<MovieModel> movieModels) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.updateMovieList(movieModels);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
