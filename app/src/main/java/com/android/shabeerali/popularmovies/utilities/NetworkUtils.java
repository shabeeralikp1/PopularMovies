package com.android.shabeerali.popularmovies.utilities;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the Movie database
 */
public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String MOVIE_DB_URL = "http://api.themoviedb.org/3/movie";

    public static final String MOVIE_POSTER_URL_W185 = "http://image.tmdb.org/t/p/w185/";

    private final static String API_KEY = "api_key";

    private final static String LANGUAGE = "language";

    private final static String MOVIES_POPULAR = "popular";

    private final static String MOVIES_TOP_RATED= "top_rated";

    public final static String MOVIES_DB_IMAGE_SIZE_W92= "w92";

    public final static String MOVIES_DB_IMAGE_SIZE_W154= "w154";

    public final static String MOVIES_DB_IMAGE_SIZE_W185= "w185";

    public final static String MOVIES_DB_IMAGE_SIZE_W342= "w342";

    public final static String MOVIES_DB_IMAGE_SIZE_W500= "w500";

    public final static String MOVIES_DB_IMAGE_SIZE_W780= "w780";

    public final static String MOVIES_DB_IMAGE_SIZE_ORIGINAL= "original";

    public static String response_language = "en_US";

    // API Request IDs

    /*
     * Request ID for getting popular movies
     */
    public final static int  GET_POPULAR_MOVIES = 0;

    /*
     * Request ID for getting top rated movies
     */
    public final static int  GET_TOP_RATED_MOVIES = 1;

    /*
     * Request ID for getting a movie details
     */
    public final static int  GET_MOVIE_DETAILS = 2;

    /*
     * Request ID for getting trailer details
     */
    public final static int  GET_TRAILER_DETAILS = 3;

    /*
     * Request ID for getting review details
     */
    public final static int  GET_REVIEW_DETAILS = 4;




    private static  String poster_image_base_url = "http://image.tmdb.org/t/p/" ;

    // API Key to be used in request
    private static  String api_key = "" ;

    // Poster image size to be used in request
    private static  String poster_image_size = "w185" ;

    // Poster image request url
    private static  String poster_image_url = "http://image.tmdb.org/t/p/w185/" ;

    /**
     * Sets the API key to be used in building requesy URLs
     *
     * @param apikey API Key received from themoviedb
     */
    public static void setApiKey(String apikey) {
        api_key = apikey;
    }


    /**
     * Sets the image size for the poster images
     *
     * @param url url for the poster image
     */
    public static void setPosterImageUrl(String url) {
        poster_image_url = url;
    }

    /**
     * Gets the image size for the poster images
     *
     * @return  poster image url
     */
    public static String getPosterImageUrl() {
        return poster_image_url ;
    }

    /**
     * Sets the image size for the poster images
     *
     * @param size size for the poster image
     */
    public static void setImageSize(String size) {
        poster_image_size = size;
        poster_image_url = poster_image_base_url + size + "/";
    }

    /**
     * Gets the image size for the poster images
     *
     * @return  poster image size
     */
    public static String getImageSize() {
        return poster_image_size ;
    }


    /**
     * Gets the language needed for server response
     *
     * @return  language used for server response
     */
    public static String getResponselanguage() {
        return response_language ;
    }

    /**
     * Sets the image size for the poster images
     *
     * @param language  language needed for server response
     */
    public static void setResponselanguage(String language) {
        response_language = language;
    }

    /**
     * Builds the URL used to talk to the moviedb server based on the given request type.
     *
     * @param requestType request type for movie APIs
     * @param movie_id  movie id
     * @return The URL to use to query the moviedb server.
     */
    public static URL getMovieRequestsUrl(int requestType, String movie_id) {
        Uri.Builder builder = null;
        Uri builtUri = null;

        if(api_key.equals("")) {
            Log.e(TAG, "API Key NOT SET. Please use NetworkUtils.setApiKey(String apikey)");
        }

        switch(requestType) {
            case GET_POPULAR_MOVIES:
                builder = Uri.parse(MOVIE_DB_URL).buildUpon()
                           .appendPath(MOVIES_POPULAR);
                break;

            case GET_TOP_RATED_MOVIES:
                builder = Uri.parse(MOVIE_DB_URL).buildUpon()
                        .appendPath(MOVIES_TOP_RATED);
                break;

            case GET_MOVIE_DETAILS:
                builder = Uri.parse(MOVIE_DB_URL).buildUpon()
                        .appendPath(movie_id);
                break;

            case GET_TRAILER_DETAILS:
                builder = Uri.parse(MOVIE_DB_URL).buildUpon()
                        .appendPath(movie_id)
                        .appendPath("videos");
                break;

            case GET_REVIEW_DETAILS:
                builder = Uri.parse(MOVIE_DB_URL).buildUpon()
                        .appendPath(movie_id)
                        .appendPath("reviews");
                break;
            default:
                Log.e(TAG, "API request not Implemented");
                return null;

        }
        builtUri = builder.appendQueryParameter(API_KEY, api_key)
                           .appendQueryParameter(LANGUAGE, response_language)
                           .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Request URL: " + url);
        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
