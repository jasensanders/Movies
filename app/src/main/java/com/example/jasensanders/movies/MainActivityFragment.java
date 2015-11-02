package com.example.jasensanders.movies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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
public class MainActivityFragment extends Fragment {
    //Adapter for GridView
    private ImageAdapter madapter;
    //Keys for Intents
    public static final String THUMB = "THUMB";
    public static final String M_POSTERURL = "M_POSTERURL";
    public static final String M_TITLE = "M_TITLE";
    public static final String M_DATE = "M_DATE";
    public static final String M_RATING = "M_RATING";
    public static final String M_SYNOPSIS = "M_SYNOPSIS";

    //ArrayList of movies
    ArrayList<Movie> roster = new ArrayList<Movie>();



    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    public MainActivityFragment() {
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragmentmenu, menu);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        GridView gridview = (GridView) rootView.findViewById(R.id.gridView);
        madapter = new ImageAdapter(getActivity(), roster);

        gridview.setAdapter(madapter);
        //Log.d(LOG_TAG, "GridViewAdapterSet");

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(getActivity(), roster.get(position).Title,
                        Toast.LENGTH_SHORT).show();
                Intent detailIntent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(THUMB, roster.get(position).Thumb)
                        .putExtra(M_TITLE, roster.get(position).Title)
                        .putExtra(M_POSTERURL, roster.get(position).Poster)
                        .putExtra(M_DATE, roster.get(position).Date)
                        .putExtra(M_RATING, roster.get(position).Rating)
                        .putExtra(M_SYNOPSIS, roster.get(position).Synopsis);
                startActivity(detailIntent);
            }
        });



        return rootView;
    }

    private void updateMovies(){
        FetchMovieTask newmovieTask = new FetchMovieTask();
        /*SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = prefs.getString(getString(R.string.pref_location_key),
                getString(R.string.pref_location_default));*/
        newmovieTask.execute("popular");
    }

    public void onStart(){
        super.onStart();
        updateMovies();
    }



    public class ImageAdapter extends ArrayAdapter<Movie> {
        private Context context;
        private LayoutInflater inflater;
        private ArrayList<Movie> moviedata;
        private final String LOG_TAG = ImageAdapter.class.getSimpleName();

        public ImageAdapter(Context context, ArrayList<Movie> data) {
            super(context, R.layout.image_tile, data);

            this.context = context;
            this.moviedata = data;

            inflater = LayoutInflater.from(context);
            //Log.d(LOG_TAG, "ImageAdapterInstantiated");
        }



        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            Movie current = moviedata.get(position);
            String url = current.Thumb;

            //Log.d(LOG_TAG, "AdapterGetViewCalled");
            if (null == convertView) {
                convertView = inflater.inflate(R.layout.image_tile, parent, false);
            }


            Picasso
                    .with(context)
                    .load(url)
                    .error(R.mipmap.ic_launcher)
                    .fit() // will explain later
                    .into((ImageView) convertView);

            return convertView;


        }
    }

    public class Movie{

        public String Thumb;
        public String Poster;
        public String Title;
        public String Date;
        public String Rating;
        public String Synopsis;

        public Movie(String Thumb, String Title, String Poster, String Date, String Rating, String Synopsis){
            this.Thumb = Thumb;
            this.Title = Title;
            this.Poster = Poster;
            this.Date = Date;
            this.Rating = Rating;
            this.Synopsis = Synopsis;
        }

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

            String thumbBaseUrl = "http://image.tmdb.org/t/p/w92/";
            String posterBaseUrl = "http://image.tmdb.org/t/p/w185/";


            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(TMDB_LIST);

            ArrayList<Movie> resultMovies = new ArrayList<Movie>();
            for(int i = 0; i < movieArray.length(); i++) {
                // Strings to hold the data default to null"
                String title = null;
                String synopsis =null;
                String thumb = null;
                String poster = null;
                String date = null;
                String rating = null;


                // Get the JSON object representing the Movie
                JSONObject tmdb_movie = movieArray.getJSONObject(i);

                title = tmdb_movie.getString(TMDB_TITLE);
                synopsis = tmdb_movie.getString(TMDB_SYNOPSIS);
                thumb = thumbBaseUrl + tmdb_movie.getString(TMDB_IMAGE);
                poster = posterBaseUrl + tmdb_movie.getString(TMDB_IMAGE);
                date = tmdb_movie.getString(TMDB_DATE);
                rating = tmdb_movie.getString(TMDB_RATING);
                Movie currentMovie = new Movie(thumb, title, poster, date, rating, synopsis );
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
            String SortFormat = null;
            String appkey = "749df891d0cc54abbb33461227d40008";
            //Popularity Descending
            String popular = "popularity.desc";
            String rating = "vote_average.desc";

            if(params[0].equals("popular")){
                SortFormat = popular;
            }else{
                SortFormat = rating;
            }


            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast

                final String TMDB_BASE_URL =
                        "http://api.themoviedb.org/3/discover/movie?";
                final String QUERY_PARAM = "sort_by";
                final String APP_KEY = "api_key";

                Uri builtUri = Uri.parse(TMDB_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, SortFormat)
                        .appendQueryParameter(APP_KEY, appkey)
                        .build();

                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Built URI " + builtUri.toString());

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
                madapter.clear();
                roster = result;
                for(Movie item : result) {
                    madapter.add(item);
                }
                // New data is back from the server.  Hooray!
            }
        }
    }
}
