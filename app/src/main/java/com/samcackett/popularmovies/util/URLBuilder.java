package com.samcackett.popularmovies.util;

import okhttp3.HttpUrl;

/**
 * Created by sam on 20/08/2017.
 */

public class URLBuilder {
    private static final String HTTP = "http";
    private static final String BASE_IMAGE_URL = "image.tmdb.org";
    private static final String DEFAULT_IMAGE_WIDTH = "w185";
    private static final String BACKGROUND_IMAGE_WIDTH = "w500";

    public static String createImageUrl(String posterPath) {
        return createImageUrl(DEFAULT_IMAGE_WIDTH, posterPath);
    }

    private static String createImageUrl(String imageWidth, String posterPath) {
        String parsedPosterPath = parseSlashFromPath(posterPath);
        HttpUrl url = new HttpUrl.Builder()
                .scheme(HTTP)
                .host(BASE_IMAGE_URL)
                .addPathSegment("t")
                .addPathSegment("p")
                .addPathSegment(imageWidth)
                .addPathSegment(parsedPosterPath)
                .build();

        return url.toString();
    }

    public static String createBackgroundUrl(String posterPath) {
        return createImageUrl(BACKGROUND_IMAGE_WIDTH, posterPath);
    }

    private static String parseSlashFromPath(String posterPath) {
        return posterPath.replace("/", "");
    }
}
