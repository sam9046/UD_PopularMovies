package com.samcackett.popularmovies.api;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.stream.JsonReader;
import com.samcackett.popularmovies.model.tmdb.TmdbResponse;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Call;
import retrofit2.Response;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by sam on 30/07/2017.
 */
public class TmdbServiceTest {

    @Rule
    public final MockWebServer server = new MockWebServer();
    private String popularMoviesJson;

    @Before
    public void setUp() throws Exception {
        popularMoviesJson = getJsonFileAsString();
    }

    private String getJsonFileAsString() throws FileNotFoundException, JsonIOException {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource("popularMovies.json");
        JsonReader jsonReader = new JsonReader(new FileReader(resource.getFile()));
        TmdbResponse response = new Gson().fromJson(jsonReader, TmdbResponse.class);
        return new Gson().toJson(response);
    }

    @Test
    public void tmdbService_shouldAppendApiKeyToHeader() throws Exception {
        TmdbService.Service service = TmdbService.getInstance(server.url(""));

        server.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json")
                .setBody(popularMoviesJson));

        Call<TmdbResponse> call = service.getPopularMovies();
        Response<TmdbResponse> response = call.execute();
        assertNotNull(response.body());

        RecordedRequest recordedRequest = server.takeRequest();
        boolean containsApiKey = recordedRequest.getPath().contains("api_key");
        assertTrue(containsApiKey);
    }

}