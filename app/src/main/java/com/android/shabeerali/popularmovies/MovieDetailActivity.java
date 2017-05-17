package com.android.shabeerali.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.shabeerali.popularmovies.data.FavoriteMovieContract;
import com.android.shabeerali.popularmovies.data.FavoriteMovieDbHelper;
import com.android.shabeerali.popularmovies.data.MovieObject;
import com.android.shabeerali.popularmovies.utilities.MovieDataJsonParser;
import com.android.shabeerali.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

import static com.android.shabeerali.popularmovies.data.FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME;

public class MovieDetailActivity extends AppCompatActivity {

    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;
    private LinearLayout mErrorLayout;
    private Button mTryAgainButton;
    private ScrollView mMovieDetailView;
    ImageView moviePoster;
    TextView movieName;
    TextView movieOverview;
    TextView movieDateRating;
    int movie_id;
    String movie_name;
    String movie_poster_path;
    private CheckBox mFavoriteMovie;
    private String[]  mTrailerKeys;
    private String[]  mReviews;

    private SQLiteDatabase mDb;

    private LinearLayout mTrailerLayout;
    private LinearLayout mReviewsLayout;

    private ListView mTrailerList;
    private ListView mReviewsList;

    private ArrayAdapter<String> trailerAdapter;
    private ArrayList<String> trailerArrayList;

    private ArrayAdapter<String> reviewsAdapter;
    private ArrayList<String> reviewsArrayList;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        moviePoster = (ImageView) findViewById(R.id.iv_detail_view);
        movieName = (TextView) findViewById(R.id.tv_movie_name);
        movieOverview = (TextView) findViewById(R.id.tv_movie_overview);
        movieDateRating = (TextView) findViewById(R.id.tv_movie_date_rating);
        mMovieDetailView = (ScrollView) findViewById(R.id.sv_movie_details);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_mv_detail_error_message_display);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_mv_detail_loading_indicator);
        mErrorLayout  = (LinearLayout) findViewById(R.id.ll_mv_detail_error_layout);
        mTryAgainButton = (Button) findViewById(R.id.bt_mv_detail_try_again);
        mTryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOnline()) {
                    new FetchMovieInfosTask().execute(Integer.toString(movie_id));
                } else {
                    showErrorMessage(MovieDetailActivity.this.getResources().getString(R.string.no_internet_connection));
                }
            }
        });

        mTrailerLayout = (LinearLayout) findViewById(R.id.ll_trailer_view);
        mReviewsLayout = (LinearLayout) findViewById(R.id.ll_review_view);
        mTrailerList = (ListView) findViewById(R.id.lv_trailer_list);
        mReviewsList = (ListView) findViewById(R.id.lv_review_list);

        trailerArrayList = new ArrayList<String>();
        trailerAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.reviews_list, trailerArrayList);

        // Here, you set the data in your ListView
        mTrailerList.setAdapter(trailerAdapter);


        reviewsArrayList = new ArrayList<String>();
        reviewsAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.reviews_list, reviewsArrayList);

        // Here, you set the data in your ListView
        mReviewsList.setAdapter(reviewsAdapter);


        mFavoriteMovie = (CheckBox) findViewById(R.id.bt_movie_favorite);
        NetworkUtils.setResponselanguage(Locale.getDefault().toString());

        Intent intentThatStartedThisActivity = getIntent();

        // Create a DB helper (this will create the DB if run for the first time)
        FavoriteMovieDbHelper dbHelper = new FavoriteMovieDbHelper(this);

        // Keep a reference to the mDb until paused or killed. Get a writable database
        // because you will be adding restaurant customers
        mDb = dbHelper.getWritableDatabase();

        if (intentThatStartedThisActivity != null) {

            movie_id = intentThatStartedThisActivity.getIntExtra("movie_id", -1);

            if(isOnline()) {
                new FetchMovieInfosTask().execute(Integer.toString(movie_id));
            } else {
                showErrorMessage(MovieDetailActivity.this.getResources().getString(R.string.no_internet_connection));
            }
        }

    }


    private void showMovieDataView(MovieObject moviesData) {
        mErrorLayout.setVisibility(View.INVISIBLE);
        mMovieDetailView.setVisibility(View.VISIBLE);

        String poster_url = NetworkUtils.getPosterImageUrl()  + moviesData.getPosterPath();
        Picasso.with(this).load(poster_url).into(moviePoster);

        movie_name = moviesData.getTitle();
        movie_poster_path = moviesData.getPosterPath();

        movieName.setText(movie_name);
        movieOverview.setText(moviesData.getOverview());

        String year = moviesData.getReleaseDate().split("-", 3)[0];
        String displayText = "";
        displayText = year;
        Double rating = moviesData.getRating();
        if(rating != 0) {
            if(!year.equals("")) {
                displayText = displayText + "  |  " + rating;
            } else {
                displayText = rating.toString();
            }
        }

        movieDateRating.setText(displayText);


        if(IsMovieInFavorites(movie_id)) {
            mFavoriteMovie.setChecked(true);
        } else {
            mFavoriteMovie.setChecked(false);
        }

        checkForTrailers();

    }

    private void showErrorMessage(String message) {
        mMovieDetailView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setText(message);
        mErrorLayout.setVisibility(View.VISIBLE);
    }

    public class FetchMovieInfosTask extends AsyncTask<String, Void, MovieObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected MovieObject doInBackground(String... params) {

            if (params.equals("-1") ) {
                return null;
            }

            String movie_id = params[0];
            //URL moviesRequestUrl = NetworkUtils.buildUrl(fetch_filter);
            URL moviesRequestUrl = NetworkUtils.getMovieRequestsUrl(NetworkUtils.GET_MOVIE_DETAILS, movie_id);

            try {
                String jsonMovieInfoResponse = NetworkUtils
                        .getResponseFromHttpUrl(moviesRequestUrl);

                MovieObject movieObject = MovieDataJsonParser
                        .parseMovieInformation(jsonMovieInfoResponse);
                return movieObject;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(MovieObject moviesData) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (moviesData != null) {
                showMovieDataView(moviesData);
            } else {
                showErrorMessage(MovieDetailActivity.this.getResources().getString(R.string.error_message));
            }
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void  handleFavourite(View view) {

        if(mFavoriteMovie.isChecked()) {
            addFavorite(movie_id, movie_name, movie_poster_path);
            Toast.makeText(this, "Added to favorites",Toast.LENGTH_SHORT).show();
        } else {
            removeFavorite(movie_id);
            Toast.makeText(this, "Removed from favorites",Toast.LENGTH_SHORT).show();
        }
    }


    private long addFavorite(int movieId, String movieName, String posterPath) {
        ContentValues cv = new ContentValues();
        cv.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID, movieId);
        cv.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_MOVIE_NAME, movieName);
        cv.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_MOVIE_POSTERPATH, posterPath);
        return mDb.insert(TABLE_NAME, null, cv);
    }


    private boolean removeFavorite(int id) {
        return mDb.delete(FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME, FavoriteMovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID + "=" + id, null) > 0;
    }


    private Cursor getAllFavorites() {
        return mDb.query(
                TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                FavoriteMovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID
        );
    }

    private boolean IsMovieInFavorites(int movie_id) {

        String[] columns = { FavoriteMovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID };
        String selection = FavoriteMovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID + " =?";
        String[] selectionArgs = {Integer.toString(movie_id)};
        String limit = "1";

        Cursor cursor = mDb.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null, limit);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;

    }

    private void checkForTrailers() {
        new FetchMovieTrailersTask().execute(Integer.toString(movie_id));

    }

    private void checkForReviews() {
        new FetchMovieReviewsTask().execute(Integer.toString(movie_id));

    }

    private void showTrailerInfo(String[] trailerKeys) {
        Log.d("SHABEER  ", "Trailers");
        for(String key :trailerKeys) {
            Log.d("SHABEER  ", "" + key);
        }

        mTrailerKeys = trailerKeys.clone();
        checkForReviews();
    }

    private void showReviewInfo(String[] reviews) {
        mReviewsLayout.setVisibility(View.VISIBLE);
        Log.d("SHABEER  ", "reviews");
        for(String key :reviews) {
            Log.d("SHABEER  ", "" + key);
            reviewsArrayList.add(key);
        }

        reviewsAdapter.notifyDataSetChanged();

        mReviews = reviews.clone();

    }

    public class FetchMovieTrailersTask extends AsyncTask<String, Void, String[]> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(String... params) {

            if (params.equals("-1") ) {
                return null;
            }

            String movie_id = params[0];
            //URL moviesRequestUrl = NetworkUtils.buildUrl(fetch_filter);
            URL trailerRequestUrl = NetworkUtils.getMovieRequestsUrl(NetworkUtils.GET_TRAILER_DETAILS, movie_id);

            try {
                String jsonMovieInfoResponse = NetworkUtils
                        .getResponseFromHttpUrl(trailerRequestUrl);

                Log.e("SHABEER", "" + jsonMovieInfoResponse);

                String[] trailerKeys = MovieDataJsonParser
                        .parseTrailerInformation(jsonMovieInfoResponse);

                Log.e("SHABEER", "" + trailerKeys);
                return trailerKeys;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] trailerKeys) {
            showTrailerInfo(trailerKeys);
        }
    }

    public class FetchMovieReviewsTask extends AsyncTask<String, Void, String[]> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(String... params) {

            if (params.equals("-1") ) {
                return null;
            }

            String movie_id = params[0];
            //URL moviesRequestUrl = NetworkUtils.buildUrl(fetch_filter);
            URL trailerRequestUrl = NetworkUtils.getMovieRequestsUrl(NetworkUtils.GET_REVIEW_DETAILS, movie_id);

            try {
                String jsonMovieInfoResponse = NetworkUtils
                        .getResponseFromHttpUrl(trailerRequestUrl);

                Log.e("SHABEER", "" + jsonMovieInfoResponse);

                String[] reviews = MovieDataJsonParser
                        .parseReviewInformation(jsonMovieInfoResponse);
                Log.e("SHABEER", "" + reviews);
                return reviews;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] reviews) {
            //mLoadingIndicator.setVisibility(View.INVISIBLE);
                showReviewInfo(reviews);
        }
    }

}


