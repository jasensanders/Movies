package com.example.jasensanders.movies;

/**
 * Created by Jasen Sanders on 016,12/16/15.
 */
public  class Movie {
    public String id;
    public String Thumb;
    public String Poster;
    public String Title;
    public String Date;
    public String Rating;
    public String Synopsis;
    public String Reviews;
    public String Trailers;

    public Movie(String id, String Thumb, String Poster, String Title,
                 String Date, String Rating, String Synopsis, String Reviews, String Trailers){

        this.id = id;
        this.Thumb = Thumb;
        this.Poster = Poster;
        this.Title = Title;
        this.Date = Date;
        this.Rating = Rating;
        this.Synopsis = Synopsis;
        this.Reviews = Reviews;
        this.Trailers = Trailers;
    }

    public String[] toArray(){
        String [] result = {this.id, this.Thumb, this.Poster, this.Title, this.Date,
            this.Rating, this.Synopsis, this.Reviews, this.Trailers};
        return result;
    }

}
