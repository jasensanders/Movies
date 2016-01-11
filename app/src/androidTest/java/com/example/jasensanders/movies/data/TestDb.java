/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.jasensanders.movies.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.jasensanders.movies.data.MovieFavContract.FavoritesEntry;

import java.util.HashSet;

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(FavoritesDBHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }

    /*
        Students: Uncomment this test once you've written the code to create the Location
        table.  Note that you will have to have chosen the same column names that I did in
        my solution for this test to compile, so if you haven't yet done that, this is
        a good time to change your column names to match mine.

        Note that this only tests that the Location table has the correct columns, since we
        give you the code for the weather table.  This test does not look at the
     */
    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(FavoritesEntry.TABLE_NAME);


        mContext.deleteDatabase(FavoritesDBHelper.DATABASE_NAME);
        SQLiteDatabase db = new FavoritesDBHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain the Favorites entry table
        assertTrue("Error: Your database was created without both the location entry and weather entry tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + FavoritesEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> locationColumnHashSet = new HashSet<String>();
        locationColumnHashSet.add(MovieFavContract.FavoritesEntry._ID);
        locationColumnHashSet.add(FavoritesEntry.COLUMN_MOVIE_ID);
        locationColumnHashSet.add(FavoritesEntry.COLUMN_THUMB);
        locationColumnHashSet.add(FavoritesEntry.COLUMN_M_POSTERURL);
        locationColumnHashSet.add(FavoritesEntry.COLUMN_M_TITLE);
        locationColumnHashSet.add(FavoritesEntry.COLUMN_M_DATE);
        locationColumnHashSet.add(FavoritesEntry.COLUMN_M_RATING);
        locationColumnHashSet.add(FavoritesEntry.COLUMN_M_SYNOPSIS);
        locationColumnHashSet.add(FavoritesEntry.COLUMN_M_REVIEWS);
        locationColumnHashSet.add(FavoritesEntry.COLUMN_M_TRAILERS);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            locationColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                locationColumnHashSet.isEmpty());
        db.close();
    }

    /*
        Students:  Here is where you will build code to test that we can insert and query the
        location database.  We've done a lot of work for you.  You'll want to look in TestUtilities
        where you can uncomment out the "createNorthPoleLocationValues" function.  You can
        also make use of the ValidateCurrentRecord function from within TestUtilities.
    */
    public long testLocationTable() {

        final String LOG_TAG = TestDb.class.getSimpleName();
        // First step: Get reference to writable database
        FavoritesDBHelper dbHelper = new FavoritesDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create ContentValues of what you want to insert

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

        ContentValues values = new ContentValues();
        values.put(FavoritesEntry.COLUMN_MOVIE_ID, MovieID);
        values.put(FavoritesEntry.COLUMN_THUMB, thumb);
        values.put(FavoritesEntry.COLUMN_M_POSTERURL, poster);
        values.put(FavoritesEntry.COLUMN_M_TITLE, title);
        values.put(FavoritesEntry.COLUMN_M_DATE,date);
        values.put(FavoritesEntry.COLUMN_M_RATING,rating);
        values.put(FavoritesEntry.COLUMN_M_SYNOPSIS, Synopsis);
        values.put(FavoritesEntry.COLUMN_M_REVIEWS,Reviews);
        values.put(FavoritesEntry.COLUMN_M_TRAILERS,Trailers);

        // Insert ContentValues into database and get a row ID back
        long locationRowId;
        locationRowId = db.insert(FavoritesEntry.TABLE_NAME, null, values);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

        // Query the database and receive a Cursor back
        String[] columns = {
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

        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                FavoritesEntry.TABLE_NAME,  // Table to Query
                columns,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // Move the cursor to a valid database row
        if (cursor.moveToFirst()) {
            // Get the value in each column by finding the appropriate column index.
            int IdIndex = cursor.getColumnIndex(FavoritesEntry.COLUMN_MOVIE_ID);
            String Mid = cursor.getString(IdIndex);

            int thumbIndex = cursor.getColumnIndex((FavoritesEntry.COLUMN_THUMB));
            String thumbUrl = cursor.getString(thumbIndex);

            int posterIndex = cursor.getColumnIndex((FavoritesEntry.COLUMN_M_POSTERURL));
            String posterUrl = cursor.getString(posterIndex);

            int titleIndex = cursor.getColumnIndex((FavoritesEntry.COLUMN_M_TITLE));
            String titleM = cursor.getString(titleIndex);

            int dateIndex = cursor.getColumnIndex(FavoritesEntry.COLUMN_M_DATE);
            String dateM = cursor.getString(dateIndex);

            int ratingIndex = cursor.getColumnIndex(FavoritesEntry.COLUMN_M_RATING);
            String ratingM = cursor.getString(ratingIndex);

            int synopsisIndex = cursor.getColumnIndex(FavoritesEntry.COLUMN_M_SYNOPSIS);
            String synopsisM = cursor.getString(synopsisIndex);

            int reviewIndex = cursor.getColumnIndex(FavoritesEntry.COLUMN_M_REVIEWS);
            String reviewM = cursor.getString(reviewIndex);

            int trailersIndex = cursor.getColumnIndex(FavoritesEntry.COLUMN_M_TRAILERS);
            String trailersM = cursor.getString(trailersIndex);


            // Hooray, data was returned!  Assert that it's the right data, and that the database
            // creation code is working as intended.
            // Then take a break.  We both know that wasn't easy.
            assertEquals(MovieID, Mid);
            assertEquals(thumb, thumbUrl);
            assertEquals(poster, posterUrl);
            assertEquals(title,titleM);
            assertEquals(date, dateM);
            assertEquals(rating,ratingM);
            assertEquals(Synopsis, synopsisM);
            assertEquals(Reviews, reviewM);
            assertEquals(Trailers, trailersM);

        } else {
            // That's weird, it works on MY machine...
            fail("No values returned :(");
        }


        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)

        // Finally, close the cursor and database
        cursor.close();
        dbHelper.close();

        return locationRowId;

    }

    /*
        Students:  Here is where you will build code to test that we can insert and query the
        database.  We've done a lot of work for you.  You'll want to look in TestUtilities
        where you can use the "createWeatherValues" function.  You can
        also make use of the validateCurrentRecord function from within TestUtilities.
     */
//    public void testWeatherTable() {
//        // First insert the location, and then use the locationRowId to insert
//        // the weather. Make sure to cover as many failure cases as you can.
//        long locationRowId = testLocationTable();
//
//
//
//        // Instead of rewriting all of the code we've already written in testLocationTable
//        // we can move this code to insertLocation and then call insertLocation from both
//        // tests. Why move it? We need the code to return the ID of the inserted location
//        // and our testLocationTable can only return void because it's a test.
//
//        // First step: Get reference to writable database
//        WeatherDbHelper dbHelper = new WeatherDbHelper(mContext);
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//
//        // Create ContentValues of what you want to insert
//        // (you can use the createWeatherValues TestUtilities function if you wish)
//        ContentValues weatherValues = TestUtilities.createWeatherValues(locationRowId);
//
//        // Insert ContentValues into database and get a row ID back
//        long weatherRowId = db.insert(WeatherEntry.TABLE_NAME, null, weatherValues);
//        assertTrue(weatherRowId != -1);
//
//        // Query the database and receive a Cursor back
//        Cursor weatherCursor = db.query(
//                WeatherEntry.TABLE_NAME,  // Table to Query
//                null, // leaving "columns" null just returns all the columns.
//                null, // cols for "where" clause
//                null, // values for "where" clause
//                null, // columns to group by
//                null, // columns to filter by row groups
//                null  // sort order
//        );
//
//        // Move the cursor to a valid database row
//
//
//        // Validate data in resulting Cursor with the original ContentValues
//        // (you can use the validateCurrentRecord function in TestUtilities to validate the
//        // query if you like)
//        if (!weatherCursor.moveToFirst()) {
//            fail("No weather data returned!");
//        }
//        assertEquals(weatherCursor.getInt(
//                weatherCursor.getColumnIndex(WeatherEntry.COLUMN_LOC_KEY)), locationRowId);
//        assertEquals(weatherCursor.getLong(
//                weatherCursor.getColumnIndex(WeatherEntry.COLUMN_DATE)), 1419033600L);
//        assertEquals(weatherCursor.getDouble(
//                weatherCursor.getColumnIndex(WeatherEntry.COLUMN_DEGREES)), 1.1);
//        assertEquals(weatherCursor.getDouble(
//                weatherCursor.getColumnIndex(WeatherEntry.COLUMN_HUMIDITY)), 1.2);
//        assertEquals(weatherCursor.getDouble(
//                weatherCursor.getColumnIndex(WeatherEntry.COLUMN_PRESSURE)), 1.3);
//        assertEquals(weatherCursor.getInt(
//                weatherCursor.getColumnIndex(WeatherEntry.COLUMN_MAX_TEMP)), 75);
//        assertEquals(weatherCursor.getInt(
//                weatherCursor.getColumnIndex(WeatherEntry.COLUMN_MIN_TEMP)), 65);
//        assertEquals(weatherCursor.getString(
//                weatherCursor.getColumnIndex(WeatherEntry.COLUMN_SHORT_DESC)), "Asteroids");
//        assertEquals(weatherCursor.getDouble(
//                weatherCursor.getColumnIndex(WeatherEntry.COLUMN_WIND_SPEED)), 5.5);
//        assertEquals(weatherCursor.getInt(
//                weatherCursor.getColumnIndex(WeatherEntry.COLUMN_WEATHER_ID)), 321);
//
//        // Finally, close the cursor and database
//        weatherCursor.close();
//        dbHelper.close();
//    }


}
