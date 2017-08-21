package com.samcackett.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.samcackett.popularmovies.model.tmdb.MovieModel;
import com.samcackett.popularmovies.movies.MovieDetailActivity;
import com.samcackett.popularmovies.util.URLBuilder;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sam on 29/07/2017.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {
    private List<MovieModel> movieModels = new ArrayList<>();
    private final Context context;

    public RecyclerViewAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_list_item, parent, false);
        return new RecyclerViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, int position) {
        holder.progressBar.setVisibility(View.VISIBLE);
        holder.movieModel = movieModels.get(position);
        Picasso.with(context)
                .load(URLBuilder.createImageUrl(movieModels.get(position).getPosterPath()))
                .error(R.drawable.error_cloud_icon)
                .into(holder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        holder.progressBar.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return movieModels.size();
    }

    public void updateMovieList(List<MovieModel> movieModels) {
        this.movieModels = movieModels;
        notifyDataSetChanged();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private static final String TAG = "RecyclerViewHolder";
        final ProgressBar progressBar;
        final ImageView imageView;
        MovieModel movieModel;

        RecyclerViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            progressBar = (ProgressBar) itemView.findViewById(R.id.card_progress_bar);
            imageView = (ImageView) itemView.findViewById(R.id.movie_thumb_img_view);
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, movieModel.getTitle() + "clicked");
            Context context = v.getContext();
            Intent intent = new Intent(context, MovieDetailActivity.class);
            intent.putExtra("MovieModel", movieModel);
            context.startActivity(intent);
        }
    }
}
