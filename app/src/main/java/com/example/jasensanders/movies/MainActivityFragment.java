package com.example.jasensanders.movies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.jasensanders.movies.data.MovieFavContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    //Adapter for GridView Standard
    private GridViewAdapter madapter;
    //Adapter for GridView Favorites
    private FavoritesAdapter fadapter;
    //Saved position set to invalid by default
    private int mPostition = GridView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";
    private GridView mGridView;
    private static final int FAVORITES_LOADER = 0;
    private boolean FavoritesView = false;
    private boolean IsTablet = false;

    //Key for Intent
    public static final String MOVIE = "MOVIE";


    //ArrayList of movies
    ArrayList<Movie> roster = new ArrayList<Movie>();






    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri dateUri, String[] bob);
    }

    public MainActivityFragment() {
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
        IsTablet = Utility.isTablet(getActivity());
        FavoritesView = Utility.isFavoritesView(getActivity());

    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragmentmenu, menu);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mGridView = (GridView) rootView.findViewById(R.id.gridView);
        //mGridView.setEmptyView(/*TODO Create xml empty view and load here*/);


        //When user clicks on an item pull Movie id from Roster or Cursor and pass to Detail Activity

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                if (FavoritesView) {

                    Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                    if (cursor != null) {
                        if(IsTablet) {
                        ((Callback) getActivity()).onItemSelected(
                                MovieFavContract.FavoritesEntry.buildMovieIdUri(cursor.getString(MovieFavContract.COL_MOVIE_ID)), null);

                        }else {

                            Uri contentUri = MovieFavContract.FavoritesEntry.buildMovieIdUri(cursor.getString(MovieFavContract.COL_MOVIE_ID));
                            Intent intent = new Intent(getActivity(), DetailActivity.class)
                                    .setData(contentUri);

                            startActivity(intent);
                        }

                    }
                } else {
                    if(IsTablet){
                        ((Callback) getActivity()).onItemSelected(null, roster.get(position).toArray());

                    }else {

                        Intent detailIntent = new Intent(getActivity(), DetailActivity.class)
                                .putExtra(MOVIE, roster.get(position).toArray());

                        startActivity(detailIntent);
                    }
                }

                mPostition = position;

            }

        });
        //If app was rotated, check saved instance state for selected key
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPostition = savedInstanceState.getInt(SELECTED_KEY);
        }


        return rootView;
    }


    private void updateMovies(){

        Context here = getActivity();
        FavoritesView = Utility.isFavoritesView(here);
        String sortPref = Utility.getPrefView(here);
        //If the Sort Setting is set to Favorites
        if(FavoritesView){
            //If the DataBase is empty, notify User and call FetchMovieTask
            if(Utility.isDBEmpty(here)){
                Toast.makeText(here, "No Favorites Saved",
                        Toast.LENGTH_SHORT).show();
            }else { //otherwise call the Loader and Load the Favorites
                getLoaderManager().initLoader(FAVORITES_LOADER, null, this);
            }
        //otherwise call FetchMovieTask and pass the Sort Prefrence
        }else{ FetchMovieTask newmovieTask = new FetchMovieTask();
            newmovieTask.execute(sortPref);}
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //When the tablet rates and the app is destroyed saved the position of the selected key in
        //the savedInstanceState.
        //If app was just started app will not have a position, so check first
        if(mPostition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPostition);
        }
        super.onSaveInstanceState(outState);
    }

    public void onStart(){
        super.onStart();
        updateMovies();
    }


    public class FetchMovieTask extends AsyncTask<String, Void, ArrayList<Movie>> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        /**
         * Take the String representing the complete result in JSON Format and
         * pull out the data we need to construct the Movie objects needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private ArrayList<Movie> getMovieDataFromJson(String movieJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String TMDB_LIST = "results";
            final String TMDB_TITLE = "original_title";
            final String TMDB_IMAGE = "poster_path";
            final String TMDB_DATE = "release_date";
            final String TMDB_RATING = "vote_average";
            final String TMDB_SYNOPSIS = "overview";
            final String TMDB_ID = "id";

            String thumbBaseUrl = "http://image.tmdb.org/t/p/w92/";
            String posterBaseUrl = "http://image.tmdb.org/t/p/w185/";


            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(TMDB_LIST);

            ArrayList<Movie> resultMovies = new ArrayList<Movie>();
            for(int i = 0; i < movieArray.length(); i++) {
                // Strings to hold the data"
                String id, thumb, poster, title, date, rating, synopsis, reviews, trailers;

                // Get the JSON object representing the Movie
                JSONObject tmdb_movie = movieArray.getJSONObject(i);

                id = tmdb_movie.getString(TMDB_ID);
                thumb = thumbBaseUrl + tmdb_movie.getString(TMDB_IMAGE);
                poster = posterBaseUrl + tmdb_movie.getString(TMDB_IMAGE);
                title = tmdb_movie.getString(TMDB_TITLE);
                date = tmdb_movie.getString(TMDB_DATE);
                rating = tmdb_movie.getString(TMDB_RATING);
                synopsis = tmdb_movie.getString(TMDB_SYNOPSIS);
                reviews = "";
                trailers = "";
                Movie currentMovie = new Movie(id, thumb, poster, title, date, rating, synopsis, reviews, trailers );
                resultMovies.add(currentMovie);

            }


            return resultMovies;

        }



        @Override
        protected ArrayList<Movie> doInBackground(String... params) {

            if(params.length == 0){
                //nothing to lookup so return null
                return null;
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String MovieJsonStr = null;
            //Sort format passed in from settings activity. Either "popular" or "rating"
            //Ascending or Descending order
            String SortFormat = null;
            //App API key from tmdb.org
            String appkey = BuildConfig.TMDB_API_KEY;
            //Settings options determine API call
            String popularD = "popularity.desc";
            String popularA = "popularity.asc";
            String ratingD = "vote_average.desc";
            String ratingA = "vote_average.asc";
            //switch based on which setting was selected in sort setting
            switch (params[0]){
                case "popularD":
                    SortFormat = popularD;
                    break;
                case "popularA":
                    SortFormat = popularA;
                    break;
                case "ratingD":
                    SortFormat = ratingD;
                    break;
                case "ratingA":
                    SortFormat = ratingA;
                    break;
                default:
                    SortFormat = popularD;
                    break;
            }



            try {
                // Construct the URL for the TMDB query
                final String TMDB_BASE_URL =
                        "http://api.themoviedb.org/3/discover/movie?";
                final String QUERY_PARAM = "sort_by";
                final String APP_KEY = "api_key";

                Uri builtUri = Uri.parse(TMDB_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, SortFormat)
                        .appendQueryParameter(APP_KEY, appkey)
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                MovieJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the Movie data, there's no point in attempting
                // to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            //Hope this works!!
            try {
                //This calls get data from Json then returns the arraylist of movies to onPostExecute
                return getMovieDataFromJson(MovieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            //This will only happen if there was an error getting or parsing the Movie data.
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> result) {

            if (result != null) {
                if(madapter != null) {
                    madapter.clear();
                   // mGridView.setOnItemClickListener(null);

                    roster = result;
                    for (Movie item : result) {
                        madapter.add(item);
                    }

                    mGridView.setAdapter(madapter);

                }
                else{
                    if(fadapter != null){fadapter = null;}

                    madapter = new GridViewAdapter(getActivity(), new ArrayList<Movie>());
                    roster = result;
                    for(Movie item: result){
                        madapter.add(item);

                    }
                    mGridView.setAdapter(madapter);

                }
                // New data is back from the server.  Hooray!
                // If user has already highlighted a selection smooth scroll to it.
                if (mPostition != GridView.INVALID_POSITION) {
                    //if user has already highlighted a selection smooth scroll to it.
                    mGridView.smoothScrollToPosition(mPostition);
                }
            }

        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // This is called when a new Loader needs to be created.  This
        // fragment only uses one loader, so we don't care about checking the id.

        // To only show current and future dates, get the String representation for today,
        // and filter the query to return weather only for dates after or including today.

        // Sort order:  Ascending, by when they were inserted.
        String sortOrder = MovieFavContract.FavoritesEntry._ID + " ASC";

        Uri getEverythingUri = MovieFavContract.FavoritesEntry.CONTENT_URI;


        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(), //context
                getEverythingUri, //uri for loader to pass to content provider
                MovieFavContract.FAVORITES_COLUMNS, //projection array to hold result of sql query
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if(fadapter == null){
            fadapter = new FavoritesAdapter(getActivity(), data, 0);
        }else{
            fadapter.swapCursor(data);
        }
        if(madapter != null){
            madapter.clear();
            madapter = null;}
        mGridView.setAdapter(fadapter);
        if (mPostition != GridView.INVALID_POSITION) {
            //If user has already highlighted a selection smooth scroll to it.
            mGridView.smoothScrollToPosition(mPostition);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }
}
