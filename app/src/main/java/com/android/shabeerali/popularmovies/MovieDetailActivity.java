package com.android.shabeerali.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.shabeerali.popularmovies.data.MovieObject;
import com.android.shabeerali.popularmovies.utilities.MovieDataJsonParser;
import com.android.shabeerali.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;

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

        Intent intentThatStartedThisActivity = getIntent();

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

        String poster_url = NetworkUtils.MOVIE_POSTER_URL  + moviesData.getBackdropPath();
        Picasso.with(this).load(poster_url).into(moviePoster);

        movieName.setText(moviesData.getTitle());
        movieOverview.setText(moviesData.getOverview());

        String year = moviesData.getReleaseDate().split("-", 3)[0];
        movieDateRating.setText(year + " | " + moviesData.getRating());
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
}


