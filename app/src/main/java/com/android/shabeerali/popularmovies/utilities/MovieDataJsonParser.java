package com.android.shabeerali.popularmovies.utilities;

import android.util.Log;

import com.android.shabeerali.popularmovies.data.MovieObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/*
 *  Utility Class for  parsing the JSON adat from moviedb server
 */
public class MovieDataJsonParser {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    public static final String TOTAL_RESULTS = "total_results";

    public static final String TOTAL_PAGES = "total_pages";

    public static final String PAGE = "page";

    public static final String RESULTS = "results";

    public static final String POSTER_PATH = "poster_path";

    public static final String ADULT = "adult";

    public static final String OVERVIEW = "overview";

    public static final String RELEASE_DATE = "release_date";

    public static final String GENRE_IDS = "genre_ids";

    public static final String ID = "id";

    public static final String ORIGINAL_TITLE = "original_title";

    public static final String ORIGINAL_LANGUAGE = "original_language";

    public static final String TITLE = "title";

    public static final String BACKDROP_PATH = "backdrop_path";

    public static final String POPULARITY = "popularity";

    public static final String VOTE_COUNT = "vote_count";

    public static final String VIDEO = "video";

    public static final String VOTE_AVERAGE = "vote_average";

    public static final String STATUS_CODE = "status_code";

    public static final String STATUS_MESSAGE = "status_message";

    public static final String TYPE = "type";

    public static final String TRAILER =  "Trailer";

    public static final String TRAILER_KEY =  "key";

    public static final String REVIEW_TOTAL_RESULTS =  "total_results";

    public static final String REVIEW_AUTHOR =  "author";

    public static final String REVIEW_CONTENT=  "content";


    /**
     * Parse the JSON response for movie collections request
     *
     * @param movieCollectionJsonStr  JSON response from the server
     * @return MovieObject array with movies detail
     */
    public static MovieObject[] parseMovieCollectionInformation(String movieCollectionJsonStr)
            throws JSONException {

        /* MovieObject array to hold movie collections data*/
        MovieObject[] parsedMoviesData = null;

        JSONObject movieCollectionJson = new JSONObject(movieCollectionJsonStr);

        /* Is there an error? */
        if (movieCollectionJson.has(STATUS_CODE)) {
            int errorCode = movieCollectionJson.getInt(STATUS_CODE);
            String error_message = movieCollectionJson.getString(STATUS_MESSAGE);

            Log.e(TAG, "API request returned error: " + errorCode + " ," + error_message);
            return null;
        }

        JSONArray moviesArray = movieCollectionJson.getJSONArray(RESULTS);

        parsedMoviesData = new MovieObject[moviesArray.length()];

        for (int i = 0; i < moviesArray.length(); i++) {
            JSONObject movieInfo = moviesArray.getJSONObject(i);
            String poster_path = movieInfo.getString(POSTER_PATH);
            int id = movieInfo.getInt(ID);
            parsedMoviesData[i] = new MovieObject();
            parsedMoviesData[i].setPosterPath(poster_path);
            parsedMoviesData[i].setId(id);
        }

        return parsedMoviesData;
    }

    /**
     * Parse the JSON response for movie detail request
     *
     * @param movieInfoJsonStr  JSON response from the server
     * @return MovieObject  with movie details
     */
    public static MovieObject parseMovieInformation(String movieInfoJsonStr)
            throws JSONException {

        /* MovieObject to hold detailed movie information */
        MovieObject parsedMoviesData = null;

        JSONObject movieInfoJson = new JSONObject(movieInfoJsonStr);

        /* Is there an error? */
        if (movieInfoJson.has(STATUS_CODE)) {
            int errorCode = movieInfoJson.getInt(STATUS_CODE);
            String error_message = movieInfoJson.getString(STATUS_MESSAGE);

            Log.e(TAG, "API request returned error: " + errorCode + " ," + error_message);
            return null;
        }

        parsedMoviesData = new MovieObject();
        String overview = movieInfoJson.getString(OVERVIEW);
        String title = movieInfoJson.getString(TITLE);
        String poster_path = movieInfoJson.getString(POSTER_PATH);
        String release_date = movieInfoJson.getString(RELEASE_DATE);
        Double rating = movieInfoJson.getDouble(VOTE_AVERAGE);
        String backdrop_path = movieInfoJson.getString(BACKDROP_PATH);
        String original_title = movieInfoJson.getString(ORIGINAL_TITLE);

        parsedMoviesData.setTitle(title);
        parsedMoviesData.setOverview(overview);
        parsedMoviesData.setPosterPath(poster_path);
        parsedMoviesData.setReleaseDate(release_date);
        parsedMoviesData.setRating(rating);
        parsedMoviesData.setBackdropPath(backdrop_path);
        parsedMoviesData.setOriginalTitle(original_title);

        return parsedMoviesData;
    }


    /**
     * Parse the JSON response for trailer request
     *
     * @param trailerInfoJsonStr  JSON response from the server
     * @return String array  with trailer keys for Youtube
     */
    public static String[] parseTrailerInformation(String trailerInfoJsonStr)
            throws JSONException {

        /* String[] to hold trailer key information */
        String[] trailersData = null;

        int trailerIndex = 0;

        JSONObject trailerInfoJson = new JSONObject(trailerInfoJsonStr);

        /* Is there an error? */
        if (trailerInfoJson.has(STATUS_CODE)) {
            int errorCode = trailerInfoJson.getInt(STATUS_CODE);
            String error_message = trailerInfoJson.getString(STATUS_MESSAGE);

            Log.e(TAG, "API request returned error: " + errorCode + " ," + error_message);
            return null;
        }

        JSONArray trailerArray = trailerInfoJson.getJSONArray(RESULTS);


        List<String> myList = new ArrayList<String>();
        //trailersData = new String[trailerArray.length()];

        for (int i = 0; i < trailerArray.length(); i++) {
            JSONObject trailerInfo = trailerArray.getJSONObject(i);
            String type = trailerInfo.getString(TYPE);
            if (type.equals(TRAILER)) {
                //trailersData[trailerIndex] = trailerInfo.getString(TRAILER_KEY);
                trailerIndex++;
                myList.add(trailerInfo.getString(TRAILER_KEY));
            }
        }

        if (trailerIndex == 0) {
            trailersData = null;
        } else {
            trailersData = myList.toArray(new String[myList.size()]);
        }

        return trailersData;

    }


    /**
     * Parse the JSON response for movie reviews request
     *
     * @param reviewsInfoJsonStr  JSON response from the server
     * @return String array  with review contents
     */
    public static String[] parseReviewInformation(String reviewsInfoJsonStr)
            throws JSONException {

           /* String[] to hold trailer key information */
        String[] reviewsData = null;

        int reviewIndex = 0;

        JSONObject reviewsInfoJson = new JSONObject(reviewsInfoJsonStr);

        /* Is there an error? */
        if (reviewsInfoJson.has(STATUS_CODE)) {
            int errorCode = reviewsInfoJson.getInt(STATUS_CODE);
            String error_message = reviewsInfoJson.getString(STATUS_MESSAGE);

            Log.e(TAG, "API request returned error: " + errorCode + " ," + error_message);
            return null;
        }

        JSONArray reviewsArray = reviewsInfoJson.getJSONArray(RESULTS);

        Log.e(TAG, "reviewsArray.length(): "  + " ," + reviewsArray.length());
        if(reviewsArray.length() > 0) {
            reviewsData = new String[reviewsArray.length()];

            for (int i = 0; i < reviewsArray.length(); i++) {
                JSONObject trailerInfo = reviewsArray.getJSONObject(i);
                reviewsData[i] = trailerInfo.getString(REVIEW_CONTENT);
            }
        }

        return reviewsData;
    }
}
