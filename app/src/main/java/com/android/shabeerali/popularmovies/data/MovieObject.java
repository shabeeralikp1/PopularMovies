package com.android.shabeerali.popularmovies.data;


/*
 *  Class for  Movie detailed information object
 */
public class MovieObject {
    private int id;
    private String poster_path;
    private String title;
    private String backdrop_path;
    private String overview;
    private double rating;
    private String release_date;
    private String original_title;

    public MovieObject() {
        this.title = "";
        this.id = 0;
        this.poster_path = "";
        this.backdrop_path = "";
        this.overview = "";
        this.release_date = "";
        this.rating = 0;
        this.original_title = "";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOriginalTitle() {
        return original_title;
    }

    public void setOriginalTitle(String original_title) {
        this.original_title = original_title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPosterPath() {
        return poster_path;
    }

    public void setPosterPath(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getBackdropPath() {
        return backdrop_path;
    }

    public void setBackdropPath(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public String getOverview() { return overview ;}

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getReleaseDate() {
        return release_date;
    }

    public void setReleaseDate(String release_date) {
        this.release_date = release_date;
    }

}
