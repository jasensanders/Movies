package com.example.jasensanders.movies.data;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.example.jasensanders.movies.utils.PollingCheck;

import java.util.Map;
import java.util.Set;

/**
 * Created by Jasen Sanders on 013,12/13/15.
 */
public class TestUtilities extends AndroidTestCase{

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }


    static ContentValues createValues() {
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

    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}
