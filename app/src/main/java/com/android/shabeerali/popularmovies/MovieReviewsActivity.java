package com.android.shabeerali.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Shabeerali on 5/22/2017.
 */

public class MovieReviewsActivity extends AppCompatActivity {

    private ListView mReviewsList;
    private ArrayAdapter<String> reviewsAdapter;
    private ArrayList<String> reviewsArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_reviews);

        mReviewsList = (ListView) findViewById(R.id.lv_review_list);
        reviewsArrayList = new ArrayList<String>();
        reviewsAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.reviews_list, reviewsArrayList);

        // Here, you set the data in your ListView
        mReviewsList.setAdapter(reviewsAdapter);


        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity != null) {

            Bundle b=intentThatStartedThisActivity.getExtras();
            String[] array=b.getStringArray("reviews");

            for(String key :array) {
                Log.d("SHABEER  ", "" + key);
                reviewsArrayList.add(key);
            }
            reviewsAdapter.notifyDataSetChanged();

        }
    }
}
