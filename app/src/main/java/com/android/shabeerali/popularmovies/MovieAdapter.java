package com.android.shabeerali.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.shabeerali.popularmovies.data.MovieObject;
import com.android.shabeerali.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;


/**
 * {@link MovieAdapter} exposes a list of movie information to a
 * {@link android.support.v7.widget.RecyclerView}
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private MovieObject[] mMoviesData;
    private final MovieAdapterOnClickHandler mClickHandler;
    private Context context;

    public MovieAdapter(Context context, MovieAdapterOnClickHandler clickHandler) {
        this.context = context;
        mClickHandler = clickHandler;
    }

    public interface MovieAdapterOnClickHandler {
        void onClick(MovieObject movieInfo);
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_grid_item, null);
        MovieAdapterViewHolder rcv = new MovieAdapterViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        String poster_url = NetworkUtils.getPosterImageUrl() + mMoviesData[position].getPosterPath();
        Picasso.with(context).load(poster_url).into(holder.moviePoster);
    }

    @Override
    public int getItemCount() {
        if (null == mMoviesData) return 0;
        return mMoviesData.length;
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView moviePoster;

        public MovieAdapterViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            moviePoster = (ImageView)itemView.findViewById(R.id.iv_movie_poster);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            MovieObject movieData = mMoviesData[adapterPosition];
            mClickHandler.onClick(movieData);
        }
    }

    public void setMoviesData(MovieObject[] moviesData) {
        mMoviesData = moviesData;
        notifyDataSetChanged();
    }
}
