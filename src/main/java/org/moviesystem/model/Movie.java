package org.moviesystem.model;

import java.util.Date;
import java.util.List;

public class Movie {
    private int id;
    private String title;
    private int runningTimeMinutes;
    private double rating;
    private Date releaseDate;
    private List<Actor> actors;
    private Director director;
    private String plot;
    private List<Review> reviews;

    public Movie() {
    }

    public Movie(int id, String title, int runningTimeMinutes, double rating,
                Date releaseDate, List<Actor> actors, Director director, String plot, List<Review> reviews) {
        this.id = id;
        this.title = title;
        this.runningTimeMinutes = runningTimeMinutes;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.actors = actors;
        this.director = director;
        this.plot = plot;
        this.reviews = reviews;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
