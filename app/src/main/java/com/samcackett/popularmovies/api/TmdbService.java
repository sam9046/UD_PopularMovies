package com.samcackett.popularmovies.api;

import android.support.annotation.NonNull;
import android.util.Log;

import com.samcackett.popularmovies.BuildConfig;
import com.samcackett.popularmovies.model.tmdb.TmdbResponse;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

/**
 * Created by sam on 30/07/2017.
 */

public class TmdbService {

    private static final String BASE_TMDB_URL = "http://api.themoviedb.org/3/";
    private static final String TAG = "TmdbService";

    public interface Service {
        @GET("movie/popular")
        Call<TmdbResponse> getPopularMovies();
        @GET("movie/top_rated")
        Call<TmdbResponse> getTopRatedMovies();
    }

    public static Service getInstance() {
        HttpUrl url = HttpUrl.parse(BASE_TMDB_URL);
        return getInstance(url);
    }

    public static Service getInstance(HttpUrl url) {

        OkHttpClient.Builder httpClient =
                new OkHttpClient.Builder();

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                Request original = chain.request();
                HttpUrl originalHttpUrl = original.url();

                String apiKey = BuildConfig.TMDB_API_KEY;
                if(apiKey.contentEquals("")) {
                    Log.e(TAG, "TMDB API KEY MISSING");
                    return null;
                }

                HttpUrl url = originalHttpUrl.newBuilder()
                        .addQueryParameter("api_key", BuildConfig.TMDB_API_KEY)
                        .build();

                // Request customization: add request headers
                Request.Builder requestBuilder = original.newBuilder()
                        .url(url);

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        return retrofit.create(Service.class);
    }


}
