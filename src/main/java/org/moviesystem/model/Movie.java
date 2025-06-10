package org.moviesystem.model;

import java.util.Date;
import java.util.List;

public class Movie {
    private int movieId;
    private String title;
    private int runningTimeMinutes;
    private double rating;
    private Date releaseDate;
    private List<Actor> actors;
    private Director director;
    private Language language;
    private List<Genre> genres;
    private String plot;
    private List<Review> reviews;

    public Movie() {
    }

    public Movie(int movieId, String title, int runningTimeMinutes, double rating,
                Date releaseDate, List<Actor> actors, Director director, 
                Language language, List<Genre> genres, String plot, List<Review> reviews) {
        this.movieId = movieId;
        this.title = title;
        this.runningTimeMinutes = runningTimeMinutes;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.actors = actors;
        this.director = director;
        this.language = language;
        this.genres = genres;
        this.plot = plot;
        this.reviews = reviews;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getRunningTimeMinutes() {
        return runningTimeMinutes;
    }

    public void setRunningTimeMinutes(int runningTimeMinutes) {
        this.runningTimeMinutes = runningTimeMinutes;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public List<Actor> getActors() {
        return actors;
    }

    public void setActors(List<Actor> actors) {
        this.actors = actors;
    }

    public Director getDirector() {
        return director;
    }

    public void setDirector(Director director) {
        this.director = director;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    @Override
    public String toString() {
        return title + " (" + (releaseDate != null ? releaseDate.getYear() + 1900 : "Unknown") + ")";
    }
}
