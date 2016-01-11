package com.example.jasensanders.movies;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.jasensanders.movies.data.MovieFavContract;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Jasen Sanders on 016,12/16/15.
 */
public class Utility {

    private final String LOG_TAG = Utility.class.getSimpleName();

    public static String getPrefView(Context c){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);

        return prefs.getString(c.getString(R.string.pref_sort_key),
                c.getString(R.string.pref_sort_default));
    }

    //This is a simple way to get async task to wait between api calls
    private void runInCircles(int cycles){
        for(int i =0; i<cycles; i++){
            int a =i;
        }

    }

    public static ContentValues makeRowValues(String id, String thumb, String Mposter,
                                              String Mtitle, String Mrelease, String Mrating,
                                              String Msynopsis, String Reviews, String Trailers){

        ContentValues insertMovie = new ContentValues();
        insertMovie.put(MovieFavContract.FavoritesEntry.COLUMN_MOVIE_ID, id);
        insertMovie.put(MovieFavContract.FavoritesEntry.COLUMN_THUMB, thumb);
        insertMovie.put(MovieFavContract.FavoritesEntry.COLUMN_M_POSTERURL, Mposter);
        insertMovie.put(MovieFavContract.FavoritesEntry.COLUMN_M_TITLE, Mtitle);
        insertMovie.put(MovieFavContract.FavoritesEntry.COLUMN_M_DATE, Mrelease);
        insertMovie.put(MovieFavContract.FavoritesEntry.COLUMN_M_RATING, Mrating);
        insertMovie.put(MovieFavContract.FavoritesEntry.COLUMN_M_SYNOPSIS, Msynopsis);
        insertMovie.put(MovieFavContract.FavoritesEntry.COLUMN_M_REVIEWS, Reviews);
        insertMovie.put(MovieFavContract.FavoritesEntry.COLUMN_M_TRAILERS, Trailers);
        return insertMovie;
    }

    public static ArrayList<String> trailerSplitter(String Trailers, String LOG_TAG){
        String[] temp;
        Log.v("Current Trailers:", Trailers);
        try{
             temp = Trailers.split(",");
        }
        catch (NullPointerException e){
            temp = null;
            Log.e(LOG_TAG, "Trailer failed to split", e );
        }

        ArrayList<String> trial;

        if(temp != null && temp.length >= 1){
            trial = new ArrayList<String>(Arrays.asList(temp));
        }else{
            trial = new ArrayList<String>();
            trial.add("none");
        }
        return trial;
    }
    public static ArrayList<String> friendlyText( ArrayList<String> urls) {
        ArrayList<String> result = new ArrayList<String>();
        if(urls.size() == 0|| urls == null || urls.get(0).equals("none")){result.add("No Trailer Available");}else {

            //This lets us present a friendly string to the user and still keep the urls for launching intents
            int start = 1;
            for (int i = 0; i < urls.size(); i++) {
                int number = start + i;
                String launch = "Play trailer " + String.valueOf(number);
                result.add(i,launch);
            }

        }
        return result;
    }
    public static ContentValues createValues() {
        String MovieID = "135397";
        String thumb = "http://image.tmdb.org/t/p/w92/jjBgi2r5cRt36xF6iNUEhzscEcb.jpg";
        String  poster = "http://image.tmdb.org/t/p/w185//jjBgi2r5cRt36xF6iNUEhzscEcb.jpg ";
        String title = "Jurassic World";
        String date = "2015-06-12";
        String rating = "6.9";
        String Synopsis = "Twenty-two years after the events of Jurassic Park, Isla Nublar now " +
                "features a fully functioning dinosaur theme park, Jurassic World, as originally " +
                "envisioned by John Hammond.";
        String Reviews = "jonlikesmoviesthatdontsuck says: I was a huge fan of the original 3 movies, they were out when I was younger, \n" +
                "and I grew up loving dinosaurs because of them. This movie was awesome, and I think it can stand as a testimonial piece towards the capabilities that Christopher Pratt has. He nailed it. \n" +
                "The graphics were awesome, the supporting cast did great and the t rex saved the child in me. 10\\\\5 stars, four thumbs up, and I hope that star wars episode VII doesn't disappoint.";
        String Trailers = "https://www.youtube.com/watch?v=1P-sUUUfamw, https://www.youtube.com/watch?v=RFinNxS5KN4";



        ContentValues favValues = new ContentValues();
        favValues.put(MovieFavContract.FavoritesEntry.COLUMN_MOVIE_ID, MovieID);
        favValues.put(MovieFavContract.FavoritesEntry.COLUMN_THUMB, thumb);
        favValues.put(MovieFavContract.FavoritesEntry.COLUMN_M_POSTERURL, poster);
        favValues.put(MovieFavContract.FavoritesEntry.COLUMN_M_TITLE, title);
        favValues.put(MovieFavContract.FavoritesEntry.COLUMN_M_DATE, date);
        favValues.put(MovieFavContract.FavoritesEntry.COLUMN_M_RATING, rating);
        favValues.put(MovieFavContract.FavoritesEntry.COLUMN_M_SYNOPSIS, Synopsis);
        favValues.put(MovieFavContract.FavoritesEntry.COLUMN_M_REVIEWS, Reviews);
        favValues.put(MovieFavContract.FavoritesEntry.COLUMN_M_TRAILERS, Trailers);

        return favValues;
    }
    public static boolean isFavoritesView(Context c){
        if(getPrefView(c).contentEquals("favorites")){
            return true;
        }
        return false;

    }

    public static boolean isPortrait(Context c){

        return c.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    public static boolean isLandscape(Context c){

        return c.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    public static boolean isTablet(Context c){
        return c.getResources().getBoolean(R.bool.isTablet);
    }

    public static boolean isPhone(Context c){
        if(isTablet(c)){ return false;}
        return true;
    }

    public static boolean isDBEmpty(Context c){
        boolean b =true;

        Cursor R = c.getContentResolver().query(MovieFavContract.FavoritesEntry.CONTENT_URI,
                MovieFavContract.FAVORITES_COLUMNS, null,null, MovieFavContract.INSERT_ASC);
        if( R != null){
            if(R.moveToFirst()){
                     b = false;}
            R.close();
        }
        return b;
    }

}
