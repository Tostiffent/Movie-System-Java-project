package org.moviesystem.model;

public class Review {
    private String text;
    public Review(String text) { this.text = text; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    @Override
    public String toString() { return text; }
} 