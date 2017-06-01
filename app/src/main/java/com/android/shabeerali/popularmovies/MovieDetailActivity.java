package com.android.shabeerali.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
    private TrailerAdapter trailerAdapter;

    private static final String TAG = MovieDetailActivity.class.getSimpleName();

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

        trailerAdapter = new TrailerAdapter(this, null);

        mTrailerList.setAdapter(trailerAdapter);

        mTrailerList.setClickable(true);
        mTrailerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                playTrailer(position);
            }
        });


        mFavoriteMovie = (CheckBox) findViewById(R.id.bt_movie_favorite);
        NetworkUtils.setResponselanguage(Locale.getDefault().toString());

        Intent intentThatStartedThisActivity = getIntent();

        FavoriteMovieDbHelper dbHelper = new FavoriteMovieDbHelper(this);

        mDb = dbHelper.getWritableDatabase();

        if (intentThatStartedThisActivity != null) {

            movie_id = intentThatStartedThisActivity.getIntExtra("movie_id", -1);

            if(isOnline()) {
                new FetchMovieInfosTask().execute(Integer.toString(movie_id));
            } else {
                showErrorMessage(MovieDetailActivity.this.getResources().getString(R.string.no_internet_connection));
            }
        }

        showFavoriteMovies();

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
        showFavoriteMovies();
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

        if(trailerKeys != null) {
            mTrailerKeys = trailerKeys.clone();

            mTrailerLayout.setVisibility(View.VISIBLE);
            trailerAdapter.setTrailersData(null);
            trailerAdapter.notifyDataSetChanged();
            trailerAdapter.setTrailersData(mTrailerKeys);
            setListViewHeightBasedOnChildren(mTrailerList);
            trailerAdapter.notifyDataSetChanged();

        }
        checkForReviews();
    }

    void playTrailer(int position) {
        String url = "https://www.youtube.com/watch?v=" + mTrailerKeys[position];
        Intent yt_play = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        Intent chooser = Intent.createChooser(yt_play , "Open With");

        if (yt_play .resolveActivity(getPackageManager()) != null) {
            startActivity(chooser);
        }
    }
    private void showReviewInfo(String[] reviews) {

        if(reviews != null) {
            mReviewsLayout.setVisibility(View.VISIBLE);
            mReviews = reviews.clone();
        }


    }

    public void  displayReviews(View view) {
        Context context = this;
        Class destinationClass = MovieReviewsActivity.class;

        Bundle b=new Bundle();
        b.putStringArray("reviews", mReviews);
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtras(b);
        startActivity(intentToStartDetailActivity);
    }

    public class FetchMovieTrailersTask extends AsyncTask<String, Void, String[]> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String[] doInBackground(String... params) {

            if (params.equals("-1") ) {
                return null;
            }

            String movie_id = params[0];
            URL trailerRequestUrl = NetworkUtils.getMovieRequestsUrl(NetworkUtils.GET_TRAILER_DETAILS, movie_id);

            try {
                String jsonMovieInfoResponse = NetworkUtils
                        .getResponseFromHttpUrl(trailerRequestUrl);
                String[] trailerKeys = MovieDataJsonParser
                        .parseTrailerInformation(jsonMovieInfoResponse);

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
        }

        @Override
        protected String[] doInBackground(String... params) {

            if (params.equals("-1") ) {
                return null;
            }

            String movie_id = params[0];
            URL trailerRequestUrl = NetworkUtils.getMovieRequestsUrl(NetworkUtils.GET_REVIEW_DETAILS, movie_id);

            try {
                String jsonMovieInfoResponse = NetworkUtils
                        .getResponseFromHttpUrl(trailerRequestUrl);

                String[] reviews = MovieDataJsonParser
                        .parseReviewInformation(jsonMovieInfoResponse);
                return reviews;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] reviews) {
                showReviewInfo(reviews);
        }
    }


    public static void setListViewHeightBasedOnChildren(ListView listView) {
        TrailerAdapter listAdapter = (TrailerAdapter)listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
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

    private void showFavoriteMovies() {
        Cursor cursor = getAllFavorites();

        MovieObject[] movieObjects;

        int element_count = cursor.getCount();

        if(element_count != 0) {

            int index = 0;
            while (cursor.moveToNext()) {

                String name = cursor.getString(cursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_MOVIE_NAME));
                Log.d(TAG, "  " + name);
            }
        }

        cursor.close();
    }

}


