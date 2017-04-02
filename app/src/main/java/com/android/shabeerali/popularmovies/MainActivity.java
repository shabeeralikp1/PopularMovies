package com.android.shabeerali.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.shabeerali.popularmovies.data.MovieObject;
import com.android.shabeerali.popularmovies.utilities.MovieDataJsonParser;
import com.android.shabeerali.popularmovies.utilities.NetworkUtils;

import java.net.URL;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    private RecyclerView mRecyclerView;
    private MovieAdapter mMoviewAdapter;
    private Context main_context;
    private RadioButton mRbMostPopular;
    private RadioGroup  mRadioGroup;
    private int mCheckedRadioId;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;
    private LinearLayout mErrorLayout;
    private Button mTryAgainButton;
    private GridLayoutManager lLayout;

    private static final String TAG = NetworkUtils.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NetworkUtils.setApiKey(this.getResources().getString(R.string.api_key));

        main_context = this;
        mRadioGroup = (RadioGroup) findViewById(R.id.rg_filter) ;
        mRbMostPopular = (RadioButton) findViewById(R.id.rb_most_popular);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mErrorLayout  = (LinearLayout) findViewById(R.id.error_layout);
        mTryAgainButton = (Button) findViewById(R.id.bt_try_again);

        mTryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMoviesData(mRadioGroup.getCheckedRadioButtonId());
            }
        });

        Configuration config = this.getResources().getConfiguration();

        NetworkUtils.setResponselanguage(Locale.getDefault().toString());

        if (config.smallestScreenWidthDp >= 720) {
            lLayout = new GridLayoutManager(MainActivity.this, 3);
            NetworkUtils.setImageSize(NetworkUtils.MOVIES_DB_IMAGE_SIZE_W780);
        } else  if (config.smallestScreenWidthDp >= 600) {
            lLayout = new GridLayoutManager(MainActivity.this,2);
            NetworkUtils.setImageSize(NetworkUtils.MOVIES_DB_IMAGE_SIZE_W500);
        } else {
            lLayout = new GridLayoutManager(MainActivity.this, 2);
            NetworkUtils.setImageSize(NetworkUtils.MOVIES_DB_IMAGE_SIZE_W185);
        }

        mRecyclerView = (RecyclerView)findViewById(R.id.rv_movie_posters);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(lLayout);

        mMoviewAdapter= new MovieAdapter(MainActivity.this, this);
        mRecyclerView.setAdapter(mMoviewAdapter);

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mMoviewAdapter.setMoviesData(null);
                loadMoviesData(group.getCheckedRadioButtonId());
            }
        });

        mCheckedRadioId = -1;
        if( savedInstanceState != null) {
            mCheckedRadioId = savedInstanceState.getInt("checkedRadioId");
            RadioButton checkedButton = (RadioButton) mRadioGroup.findViewById(mCheckedRadioId);
            checkedButton.setChecked(true);
        } else {
            mRbMostPopular.setChecked(true);
        }
    }
    void loadMoviesData(int checkedRadioId) {
        String filter = "popular";
        switch (checkedRadioId) {
            case R.id.rb_most_popular:
                filter = "popular";
                break;
            case R.id.rb_top_rated:
                filter = "top_rated";
        }

        if(isOnline()) {
            showMovieDataView();
            new FetchMovieCollectionsTask().execute(filter);
        } else {
            showErrorMessage(this.getResources().getString(R.string.no_internet_connection));
        }
    }

    private void showMovieDataView() {
        mErrorLayout.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage(String message) {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setText(message);
        mErrorLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(MovieObject movieData) {
        Context context = this;
        Class destinationClass = MovieDetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra("movie_id", movieData.getId());
        startActivity(intentToStartDetailActivity);
    }

    public class FetchMovieCollectionsTask extends AsyncTask<String, Void, MovieObject[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected MovieObject[] doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            String fetch_filter = params[0];
            int requestId = fetch_filter.equals("top_rated")? NetworkUtils.GET_TOP_RATED_MOVIES : NetworkUtils.GET_POPULAR_MOVIES;
            URL moviesRequestUrl = NetworkUtils.getMovieRequestsUrl(requestId, "");

            try {
                String jsonMovieInfoResponse = NetworkUtils
                        .getResponseFromHttpUrl(moviesRequestUrl);
                if (jsonMovieInfoResponse != null) {
                    MovieObject[] movieObjects = MovieDataJsonParser
                            .parseMovieCollectionInformation(jsonMovieInfoResponse);
                    return movieObjects;
                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(MovieObject[] moviesData) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (moviesData != null) {
                showMovieDataView();
                mMoviewAdapter.setMoviesData(moviesData);
            } else {
                showErrorMessage(main_context.getResources().getString(R.string.error_message));
            }
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        mCheckedRadioId = mRadioGroup.getCheckedRadioButtonId();
        savedInstanceState.putInt("checkedRadioId", mCheckedRadioId);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCheckedRadioId = savedInstanceState.getInt("checkedRadioId");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
