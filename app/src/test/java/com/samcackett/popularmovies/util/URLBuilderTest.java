package com.samcackett.popularmovies.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by sam on 20/08/2017.
 */
public class URLBuilderTest {

    @Test
    public void createPosterUrl() throws Exception {
        String path = "test";
        String s = URLBuilder.createImageUrl(path);
        assertNotNull(s);
        String posterBaseUrl = "http://image.tmdb.org/t/p/w185/";
        assertEquals(posterBaseUrl + path, s);

        boolean containsCorrectDimension = s.contains("w185");
        assertTrue(containsCorrectDimension);
    }

    @Test
    public void createBackgroundUrl() throws Exception {
        String path = "test";
        String s = URLBuilder.createBackgroundUrl(path);
        assertNotNull(s);
        String backgroundBaseUrl = "http://image.tmdb.org/t/p/w500/";
        assertEquals(backgroundBaseUrl + path, s);

        boolean containsCorrectDimension = s.contains("w500");
        assertTrue(containsCorrectDimension);
    }



}