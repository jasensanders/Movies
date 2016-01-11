package com.example.jasensanders.movies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Jasen Sanders on 012,12/12/15.
 */
public class MovieFavContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.example.jasensanders.movies";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.example.android.sunshine.app/weather/ is a valid path for
    // looking at weather data. content://com.example.android.sunshine.app/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.
    public static final String PATH_FAVORITES = "favorites";

    public static final String DATE_FORMAT = "yyyy-MM-dd";

//    /**
//     * Converts Date class to a string representation, used for easy comparison and database lookup.
//     * @param date The input date
//     * @return a DB-friendly representation of the date, using the format defined in DATE_FORMAT.
//     */
//    public static String getDbDateString(Date date){
//        // Because the API returns a unix timestamp (measured in seconds),
//        // it must be converted to milliseconds in order to be converted to valid date.
//        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
//        return sdf.format(date);
//    }
    public static final String[] FAVORITES_COLUMNS = {
        FavoritesEntry._ID,
        FavoritesEntry.COLUMN_MOVIE_ID,
        FavoritesEntry.COLUMN_THUMB,
        FavoritesEntry.COLUMN_M_POSTERURL,
        FavoritesEntry.COLUMN_M_TITLE,
        FavoritesEntry.COLUMN_M_DATE,
        FavoritesEntry.COLUMN_M_RATING,
        FavoritesEntry.COLUMN_M_SYNOPSIS,
        FavoritesEntry.COLUMN_M_REVIEWS,
        FavoritesEntry.COLUMN_M_TRAILERS
    };
    //Sort Order by Inserted Ascending by _ID
    public static final String INSERT_ASC = "_ID ASC";

    public static final int COL_ID =0;
    public static final int COL_MOVIE_ID = 1;
    public static final int COL_THUMB =2;
    public static final int COL_POSTER =3;
    public static final int COL_TITLE =4;
    public static final int COL_DATE =5;
    public static final int COL_RATING =6;
    public static final int COL_SYNOPSIS =7;
    public static final int COL_REVIEWS =8;
    public static final int COL_TRAILERS = 9;



    /*
        Inner class that defines the table contents of the location table
        Students: This is where you will add the strings.  (Similar to what has been
        done for WeatherEntry)
     */
    public static final class FavoritesEntry implements BaseColumns {
        public static final String TABLE_NAME = "favorites";

        // The MOVIE_ID setting string is what will be sent to themoviedatabase.org
        // as the review and trailers.

        //movie ID number as a string
        public static final String COLUMN_MOVIE_ID = "MOVIE_ID";
        //Url for the thumb image
        public static final String COLUMN_THUMB = "THUMB";
        //Url for the poster image
        public static final String COLUMN_M_POSTERURL = "M_POSTERURL";
        //Title String
        public static final String COLUMN_M_TITLE = "M_TITLE";
        //Date String in the format yyyy-mm-dd
        public static final String COLUMN_M_DATE = "M_DATE";
        //rating as a String in the format n.nn (out of 10.0)
        public static final String COLUMN_M_RATING = "M_RATING";
        //String of the Short synopsis
        public static final String COLUMN_M_SYNOPSIS = "M_SYNOPSIS";
        //String of all available reviews in format: "Author Says: "+ content separated by "///"
        public static final String COLUMN_M_REVIEWS = "M_REVIEWS";
        //String of Trailer youtube urls. comma separated.
        public static final String COLUMN_M_TRAILERS = "M_TRAILERS";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITES;

        //Build URI based on row_id of movie in database
        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        //Build URI based on MOVIE_ID
        public static Uri buildMovieIdUri(String Movie_Id) {

            return CONTENT_URI.buildUpon().appendPath(Movie_Id).build();
        }
        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }


    }
}
