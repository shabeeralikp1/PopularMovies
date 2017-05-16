package com.android.shabeerali.popularmovies.data;

import android.provider.BaseColumns;

public class FavoriteMovieContract {

    public static final class FavoriteMovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "favoritemovie";
        public static final String COLUMN_MOVIE_ID = "movieId";
        public static final String COLUMN_MOVIE_NAME = "movieName";
        public static final String COLUMN_MOVIE_POSTERPATH = "posterPath";
    }

}
