package com.example.jasensanders.movies.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by Jasen Sanders on 012,12/12/15.
 */
public class FavoritesProvider  extends ContentProvider{

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private FavoritesDBHelper mOpenHelper;
    //For selecting all Favorite Movies and Deleting/updating all favorites
    static final int MOVIE_ALL = 100;
    //For Selecting one Favorite Movie or Deleting/updating one movie
    static final int MOVIE_WITH_ID = 101;


    private static final SQLiteQueryBuilder sFavoriteByMovieQueryBuilder;

    static{
        sFavoriteByMovieQueryBuilder = new SQLiteQueryBuilder();

        //Set The Tables!!
        sFavoriteByMovieQueryBuilder.setTables(
                MovieFavContract.FavoritesEntry.TABLE_NAME );
    }

    //Selection of individual row by Movie ID according to theMovieDataBase.org
    private static final String sMovieIdSelection =
            MovieFavContract.FavoritesEntry.TABLE_NAME+
                    "." + MovieFavContract.FavoritesEntry.COLUMN_MOVIE_ID + " = ? ";
    private static final String sAllFavoritesSelection = null;



    private Cursor getMovieById(Uri uri, String[] projection, String sortOrder) {
        String MovieId = MovieFavContract.FavoritesEntry.getMovieIdFromUri(uri);

        //Which Row?
        String selection = sMovieIdSelection;
        //Row(s) reference
        String[] selectionArgs = new String[]{MovieId};

        return sFavoriteByMovieQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getAllFavorites(
            Uri uri, String[] projection, String sortOrder) {


        return sFavoriteByMovieQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sAllFavoritesSelection,
                null,
                null,
                null,
                sortOrder
        );
    }

    /*
        Students: Here is where you need to create the UriMatcher. This UriMatcher will
        match each URI to the WEATHER, WEATHER_WITH_LOCATION, WEATHER_WITH_LOCATION_AND_DATE,
        and LOCATION integer constants defined above.  You can test this by uncommenting the
        testUriMatcher test within TestUriMatcher.
     */
    static UriMatcher buildUriMatcher() {
        // 1) The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case. Add the constructor below.
        final UriMatcher wURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieFavContract.CONTENT_AUTHORITY;


        // 2) Use the addURI function to match each of the types.  Use the constants from
        // MovieFavContract to help define the types to the UriMatcher.
        //path requests
        wURIMatcher.addURI(authority,MovieFavContract.PATH_FAVORITES,MOVIE_ALL );
        wURIMatcher.addURI(authority, MovieFavContract.PATH_FAVORITES + "/*",MOVIE_WITH_ID);

        // 3) Return the new matcher!
        return wURIMatcher;
    }

    /*
        Students: We've coded this for you.  We just create a new WeatherDbHelper for later use
        here.
     */
    @Override
    public boolean onCreate() {
        mOpenHelper = new FavoritesDBHelper(getContext());
        return true;
    }

    /*
        Students: Here's where you'll code the getType function that uses the UriMatcher.  You can
        test this by uncommenting testGetType in TestProvider.

     */
    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case MOVIE_WITH_ID:
                return MovieFavContract.FavoritesEntry.CONTENT_ITEM_TYPE;
            case MOVIE_ALL:
                return MovieFavContract.FavoritesEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "favorites/#"
            case MOVIE_WITH_ID:
            {
                retCursor = getMovieById(uri, projection, sortOrder);
                break;
            }
            // "favorites"
            case MOVIE_ALL: {
                retCursor = getAllFavorites(uri, projection, sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    /*
        Student: Add the ability to insert Locations to the implementation of this function.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {

            case MOVIE_ALL: {
                long _id = db.insert(MovieFavContract.FavoritesEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieFavContract.FavoritesEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri+ String.valueOf(_id));
                break;

            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Student: Start by getting a writable database
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsDeleted;

        // Student: Use the uriMatcher to match the WEATHER and LOCATION URI's we are going to
        // handle.  If it doesn't match these, throw an UnsupportedOperationException.
        final int match = sUriMatcher.match(uri);
        switch (match){
            case MOVIE_WITH_ID:
                rowsDeleted = db.delete(
                        MovieFavContract.FavoritesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIE_ALL:
                rowsDeleted = db.delete(
                        MovieFavContract.FavoritesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        // Student: A null value deletes all rows.  In my implementation of this, I only notified
        // the uri listeners (using the content resolver) if the rowsDeleted != 0 or the selection
        // is null.
        // Oh, and you should notify the listeners here.
        if(selection == null||rowsDeleted!=0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Student: return the actual rows deleted
        return rowsDeleted;
    }


    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Student: This is a lot like the delete function.  We return the number of rows impacted
        // by the update.
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIE_WITH_ID:
                rowsUpdated = db.update(MovieFavContract.FavoritesEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE_ALL:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieFavContract.FavoritesEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
