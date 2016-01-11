package com.example.jasensanders.movies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.jasensanders.movies.data.MovieFavContract.FavoritesEntry;

/**
 * Created by Jasen Sanders on 012,12/12/15.
 */
public class FavoritesDBHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "favorites.db";

    public FavoritesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " + FavoritesEntry.TABLE_NAME + " (" +
                FavoritesEntry._ID + " INTEGER PRIMARY KEY," +
                FavoritesEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                FavoritesEntry.COLUMN_THUMB + " TEXT NOT NULL, " +
                FavoritesEntry.COLUMN_M_POSTERURL + " TEXT NOT NULL, " +
                FavoritesEntry.COLUMN_M_TITLE + " TEXT NOT NULL, " +
                FavoritesEntry.COLUMN_M_DATE + " TEXT NOT NULL, " +
                FavoritesEntry.COLUMN_M_RATING + " TEXT NOT NULL, " +
                FavoritesEntry.COLUMN_M_SYNOPSIS + " TEXT NOT NULL, " +
                FavoritesEntry.COLUMN_M_REVIEWS + " TEXT NOT NULL, " +
                FavoritesEntry.COLUMN_M_TRAILERS + " TEXT NOT NULL, " +
                "UNIQUE (" + FavoritesEntry.COLUMN_MOVIE_ID +") ON CONFLICT REPLACE"+
                " );";
        db.execSQL(SQL_CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        db.execSQL("DROP TABLE IF EXISTS " + FavoritesEntry.TABLE_NAME);
        onCreate(db);
    }
}
