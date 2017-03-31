package com.android.shabeerali.popularmovies.utilities;

import com.android.shabeerali.popularmovies.data.MovieObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;


public class MovieDataJsonParser {

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

    public static final String VIOTE_COUNT = "vote_count";

    public static final String VIDEO = "video";

    public static final String VOTE_AVERAGE = "vote_average";

    public static final String OWM_MESSAGE_CODE = "cod";

    public static MovieObject[] parseMovieCollectionInformation(String movieCollectionJsonStr)
            throws JSONException {

        /* MovieObject array to hold movie collections data*/
        MovieObject[] parsedMoviesData = null;

        JSONObject movieCollectionJson = new JSONObject(movieCollectionJsonStr);

        /* Is there an error? */
        if (movieCollectionJson.has(OWM_MESSAGE_CODE)) {
            int errorCode = movieCollectionJson.getInt(OWM_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
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

    public static MovieObject parseMovieInformation(String movieInfoJsonStr)
            throws JSONException {

        /* MovieObject to hold detailed movie information */
        MovieObject parsedMoviesData = null;

        JSONObject movieInfoJson = new JSONObject(movieInfoJsonStr);

        /* Is there an error? */
        if (movieInfoJson.has(OWM_MESSAGE_CODE)) {
            int errorCode = movieInfoJson.getInt(OWM_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }


        parsedMoviesData = new MovieObject();
        String overview = movieInfoJson.getString(OVERVIEW);
        String title = movieInfoJson.getString(TITLE);
        String poster_path = movieInfoJson.getString(POSTER_PATH);
        String release_date = movieInfoJson.getString(RELEASE_DATE);
        Double rating = movieInfoJson.getDouble(VOTE_AVERAGE);
        String backdrop_path = movieInfoJson.getString(BACKDROP_PATH);

        parsedMoviesData.setTitle(title);
        parsedMoviesData.setOverview(overview);
        parsedMoviesData.setPosterPath(poster_path);
        parsedMoviesData.setReleaseDate(release_date);
        parsedMoviesData.setRating(rating);
        parsedMoviesData.setBackdropPath(backdrop_path);

        return parsedMoviesData;
    }
}