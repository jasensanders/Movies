package com.example.jasensanders.movies;

import android.content.ContentValues;
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
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jasensanders.movies.data.MovieFavContract;
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
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = DetailActivityFragment.class.getSimpleName();

    //State (default standard view on a Phone)
    private Context mContext;
    private boolean FavoritesDetailView = false;
    private boolean IsPhone = true;

    //Detail Variables
    private ShareActionProvider mShareActionProvider;
    private ArrayAdapter<String> mTrailerAdapter;
    private String Trailers;
    private String Reviews;
    private ArrayList<String> trailerArray;
    private ArrayList<String> trailerLaunchText;

    //Movie Details Passed from MainActivityFragment
    private String[] MovieDetails;
    private Uri mUri;

    //Loader Constants
    static final String DETAIL_URI = "URI";
    static final String DETAIL_ARRAY = "ARRAY";
    private static final int DETAIL_LOADER = 0;

    //Share URL
    private String TrailerShare;

    //Views
    private View detailHeader;
    private View detailFooter;
    private TextView mReviews;
    private ListView trailerList;
    private TextView mTitle;
    private TextView mDate;
    private ImageView mPoster;
    private TextView mRating;
    private TextView mSynopsis;
    private Button FavoritesButton;




    public DetailActivityFragment() {
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
        //Uris are only for Favorites
        //2 ways to get handed a uri in fragment arguments (twoPane) or in intent set data(regular)
        //Decide how to handle that or if to just get the intents and check if this was launched
        //by the regular gird view.
        mContext = getActivity();
        IsPhone = Utility.isPhone(getActivity());
        FavoritesDetailView = Utility.isFavoritesView(getActivity());

        //Get the intent
        Intent detailIntent = getActivity().getIntent();

        //Initialize variables according to Device type and View
        if (IsPhone && !FavoritesDetailView) {
            MovieDetails = detailIntent.getStringArrayExtra("MOVIE");

        }else if (IsPhone && FavoritesDetailView) {
            mUri = detailIntent.getData();

        }else if (!IsPhone && !FavoritesDetailView){
            Bundle arguments = getArguments();
            //This might be null if nothing has been selected yet
            if(arguments != null){MovieDetails = arguments.getStringArray(DETAIL_ARRAY);
            }
            //else{MovieDetails = new String[]();}


        }else if(!IsPhone && FavoritesDetailView){
            Bundle arguments = getArguments();
            //This might be null if nothing has been selected yet
            if(arguments!= null){
                mUri = arguments.getParcelable(DETAIL_URI);
            }
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mTrailerAdapter = new ArrayAdapter<String>(getActivity(), R.layout.trailer_tile, R.id.list_item_trailer_text, new ArrayList<String>());
        //Inflate the views
        View detailView = inflater.inflate(R.layout.fragment_detail, container, false);
        detailHeader = inflater.inflate(R.layout.detail_header, container, false);
        detailFooter = inflater.inflate(R.layout.reviews_tile, container, false);
        FavoritesButton = (Button) detailHeader.findViewById(R.id.FavButton);



        //Fill in the View References
        mReviews = (TextView) detailFooter.findViewById(R.id.reviews_textview);
        mTitle = (TextView) detailHeader.findViewById(R.id.movieTitle);
        mPoster = (ImageView) detailHeader.findViewById(R.id.posterView);
        mDate = (TextView) detailHeader.findViewById(R.id.releaseDate);
        mRating = (TextView) detailHeader.findViewById(R.id.rating);
        mSynopsis = (TextView) detailHeader.findViewById(R.id.synopsis);

        trailerList = (ListView) detailView.findViewById(R.id.listview_trailer);
        trailerList.addHeaderView(detailHeader);
        trailerList.addFooterView(detailFooter);
        trailerList.setAdapter(mTrailerAdapter);
        trailerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                if (mTrailerAdapter.getItem(position - 1).equals("No Trailer Available")) {
                    Toast.makeText(getActivity(), "No Trailer to Launch.", Toast.LENGTH_SHORT).show();
                } else {
                    String TrailerUrl = trailerArray.get(position - 1);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(TrailerUrl));
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(intent);
                    }

                }

            }
        });



        return detailView;
    }

    public void onStart() {
        super.onStart();
        updateDetails();

    }


    public void updateDetails(){

        if(FavoritesDetailView){
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        }else{
            FetchDetailsTask task = new FetchDetailsTask();
            if(MovieDetails != null){task.execute(MovieDetails[0]);}
        }
        trailerList.setAdapter(mTrailerAdapter);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detail_fragment_menu, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareTrailerIntent());
        }
    }

    private Intent createShareTrailerIntent() {
        if(TrailerShare != null) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, TrailerShare);
            return shareIntent;
        }else{
            String placeHolder = "https://www.youtube.com/watch?v=1P-sUUUfamw";
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, placeHolder);
            return shareIntent;
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (null != mUri) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    MovieFavContract.FAVORITES_COLUMNS,
                    null,
                    null,
                    null
            );

        }
        return null;
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {

            //Get the data from the Cursor
            final String id = data.getString(MovieFavContract.COL_MOVIE_ID);
            final String Thumb = data.getString(MovieFavContract.COL_THUMB);
            final String poster = data.getString(MovieFavContract.COL_POSTER);
            final String movietitle = data.getString(MovieFavContract.COL_TITLE);
            final String date = data.getString(MovieFavContract.COL_DATE);
            final String rating = data.getString(MovieFavContract.COL_RATING);
            final String synopsis = data.getString(MovieFavContract.COL_SYNOPSIS);
            Trailers = data.getString(MovieFavContract.COL_TRAILERS);
            Reviews = data.getString(MovieFavContract.COL_REVIEWS);

            //Fill in the Header Views
            mTitle.setText(movietitle);
            Picasso
                    .with(getActivity())
                    .load(poster)
                    .fit()
                    .into(mPoster);
            mDate.setText("Release Date: " + date);
            mRating.setText("User Rating: " + rating + "/10.0");
            mSynopsis.setText("Overview: \n\n" + synopsis);
            FavoritesButton.setText("Remove Favorite");

            //Fill in the Footer Views
            mReviews.setText(Reviews);

            //Make A new trailer adapter and Add header and footer to listView, set adapter and onclick

            //Process Trailers and Fill the adapter
            trailerArray = Utility.trailerSplitter(Trailers, LOG_TAG);
            trailerLaunchText = Utility.friendlyText(trailerArray);
            mTrailerAdapter.clear();
            for(int i =0; i<trailerLaunchText.size(); i++){
                mTrailerAdapter.add(trailerLaunchText.get(i));
            }

            //Add header and footer and set adapter


            //Set the Trailer share url
            if(trailerArray.get(0).contentEquals("none")){TrailerShare = "https://www.imdb.com";}
            else{TrailerShare = trailerArray.get(0);}

            //Set onClick for favorites button
            FavoritesButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //If we click this button now then we delete it.
                    Uri deleteMovie = MovieFavContract.FavoritesEntry.buildMovieIdUri(id);
                    int rowsDeleted;
                    rowsDeleted = getActivity().getContentResolver().delete(deleteMovie,
                            MovieFavContract.FavoritesEntry.COLUMN_MOVIE_ID + " = ?",
                            new String[]{id});
                    if (rowsDeleted == 1) {
                        Toast.makeText(getActivity(), "Movie removed from Favorites.", Toast.LENGTH_SHORT).show();
                    }

                }

            });




            // If onCreateOptionsMenu has already happened, we need to update the share intent now.
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareTrailerIntent());
            }




        }
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }




    public class FetchDetailsTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchDetailsTask.class.getSimpleName();


        protected String[] doInBackground(String... params) {

            if (params[0] == null) {
                return null;
            }
            String id = params[0];
            final int RESULT_SIZE = 2;
            String[] result = new String[RESULT_SIZE];

            HttpURLConnection urlConnectionR = null;
            HttpURLConnection urlConnectionT = null;
            BufferedReader readerR = null;
            BufferedReader readerT = null;


            // Will contain the raw JSON response as a string.
            String ReviewJsonStr = null;
            String TrailerJsonStr = null;
            String appkeyR = BuildConfig.TMDB_API_KEY;

            try {
                // Construct the URLs for the TMDB.org query

                final String TMDB_BASE_URL =
                        "http://api.themoviedb.org/3/movie/";
                final String QUERY_PARAMR = "reviews";
                final String QUERY_PARAMT = "videos";
                final String APP_KEY = "api_key";

                Uri builtUriR = Uri.parse(TMDB_BASE_URL).buildUpon()
                        .appendEncodedPath(id)
                        .appendEncodedPath(QUERY_PARAMR)
                        .appendQueryParameter(APP_KEY, appkeyR)
                        .build();

                URL urlR = new URL(builtUriR.toString());

                Uri builtUriT = Uri.parse(TMDB_BASE_URL).buildUpon()
                        .appendEncodedPath(id)
                        .appendEncodedPath(QUERY_PARAMT)
                        .appendQueryParameter(APP_KEY, appkeyR)
                        .build();

                URL urlT = new URL(builtUriT.toString());

                // Create the request to TMDB, and open the connection
                urlConnectionR = (HttpURLConnection) urlR.openConnection();
                urlConnectionR.setRequestMethod("GET");
                urlConnectionR.connect();

                // Read the input stream into a String
                InputStream inputStreamR = urlConnectionR.getInputStream();
                StringBuffer bufferR = new StringBuffer();
                if (inputStreamR == null) {
                    // Nothing to do.
                    return null;
                }
                readerR = new BufferedReader(new InputStreamReader(inputStreamR));

                String line;
                while ((line = readerR.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging. Were not doing this right now cause it will confuse the reader.
                    //For now we will just append the line. (Logging the result instead of appending "\n")
                    bufferR.append(line);
                }

                if (bufferR.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                ReviewJsonStr = bufferR.toString();

                urlConnectionT = (HttpURLConnection) urlT.openConnection();
                urlConnectionT.setRequestMethod("GET");
                urlConnectionT.connect();

                // Read the input stream into a String
                InputStream inputStreamT = urlConnectionT.getInputStream();
                StringBuffer bufferT = new StringBuffer();
                if (inputStreamT == null) {
                    // Nothing to do.
                    return null;
                }
                readerT = new BufferedReader(new InputStreamReader(inputStreamT));

                String lineT;
                while ((lineT = readerT.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging. Were not doing this right now cause it will confuse the reader.
                    //For now we will just append the line. (Logging the result instead of appending "\n")
                    bufferT.append(lineT);
                }

                if (bufferT.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                TrailerJsonStr = bufferT.toString();
                Log.v(LOG_TAG, TrailerJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error Fetching details from URLs ", e);
                // If the code didn't successfully get the Movie data, there's no point in attempting
                // to parse it.
                return null;
            } finally {
                if (urlConnectionR != null) {urlConnectionR.disconnect();}
                if(urlConnectionT != null){urlConnectionT.disconnect();}
                if (readerR != null) {
                    try {
                        readerR.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing review stream", e);
                    }
                }
                if(readerT != null) {
                    try {
                        readerT.close();
                    } catch (final IOException f) {
                        Log.e(LOG_TAG, "Error closing trailer stream", f);
                    }
                }
            }
            //Hope this works!!
            try {
                result[0] = parseTrailerJson(TrailerJsonStr);
                result[1] = parseReviewJson(ReviewJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return result;

        }

        protected void onPostExecute(String[] result){

            //Load Results into variables
            try {
                Trailers = result[0];
                mTrailerAdapter.clear();
                trailerArray = Utility.trailerSplitter(result[0], LOG_TAG);
                trailerLaunchText = Utility.friendlyText(trailerArray);
                for(int i =0; i<trailerLaunchText.size(); i++){
                    mTrailerAdapter.add(trailerLaunchText.get(i));
                }

            }
            catch (NullPointerException e) {
                Log.e("RESULT:0", "Trailers result[0] is null",e);

            }
            try{
                Reviews = result[1];
                mReviews.setText(result[1]);
            }
            catch (NullPointerException f){
                Log.e("RESULT:1", "Reviews result[1] is null",f);
            }

            //Set the Trailer share url
            if(trailerArray.get(0).contentEquals("none")){TrailerShare = "https://www.imdb.com";}
            else{TrailerShare = trailerArray.get(0);}


            //Fill in Data for insert
            final String id = MovieDetails[0];
            final String thumb = MovieDetails[1];


            //Fill in the Header
            final String Mtitle = MovieDetails[3];
            mTitle.setText(Mtitle);
            final String Mposter = MovieDetails[2];
            Picasso
                    .with(getActivity())
                    .load(Mposter)
                    .fit()
                    .into(mPoster);
            final String Mrelease = MovieDetails[4];
            mDate.setText("Release Date: " + Mrelease);
            final String Mrating = MovieDetails[5];
            mRating.setText("User Rating: " + Mrating + "/10.0");
            final String Msynopsis = MovieDetails[6];
            mSynopsis.setText("Overview: \n\n" + Msynopsis);

            //Set button text
            FavoritesButton.setText("Add to Favorites");

            //OnClick of Favorites button insert into data base
            FavoritesButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //If its already in the DataBase

                    long rowId;
                    Cursor AlreadyInserted = getContext().getContentResolver().query(
                            MovieFavContract.FavoritesEntry.buildMovieIdUri(id),
                            new String[]{MovieFavContract.FavoritesEntry._ID},
                            MovieFavContract.FavoritesEntry.COLUMN_MOVIE_ID + " = ?",
                            new String[]{id},
                            null);
                    if (AlreadyInserted != null && AlreadyInserted.moveToFirst()) {
                        rowId = AlreadyInserted.getLong(MovieFavContract.COL_ID);
                        Toast.makeText(getActivity(), "Movie already in Favorites. Row:" + String.valueOf(rowId), Toast.LENGTH_SHORT).show();
                        AlreadyInserted.close();
                        //Otherwise its not in the DataBase so push to ContentProvider DataBase
                    } else {
                        // Uri for Result of insert.

                        Uri returned;
                        //Make some Content Values and fill with data
                        ContentValues insertMovie = Utility.makeRowValues(id, thumb, Mposter, Mtitle,
                                Mrelease, Mrating, Msynopsis, Reviews, Trailers);

                        //Toast.makeText(getActivity(),dump, Toast.LENGTH_LONG ).show();
                        //ContentValues test = Utility.createValues();

                        //Call insert on the Content Provider and push data to DB
                        returned = getContext().getContentResolver().insert(MovieFavContract.FavoritesEntry.CONTENT_URI, insertMovie);
                        if (returned != null) {
                            Toast.makeText(getActivity(), "Movie added to Favorites.", Toast.LENGTH_SHORT).show();
                        }
                    }

                }

            });

        }



        private String parseTrailerJson(String jsonStr)throws JSONException {
            String output = "";
            try{
                if(jsonStr==null){return "none";}else {
                    StringBuffer bufferPT = new StringBuffer();
                    final String TRAILER_LIST = "results";
                    final String TRAILER_KEY = "key";
                    final String TRAILER_BASE_URL = "https://www.youtube.com/watch?v=";
                    final String SEPARATOR = ",";
                    JSONObject result = new JSONObject(jsonStr);
                    JSONArray list = result.getJSONArray(TRAILER_LIST);
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject tmdb_trailer = list.getJSONObject(i);
                        String youtubeKey = tmdb_trailer.getString(TRAILER_KEY);
                        if (i == list.length() - 1) {
                            bufferPT.append(TRAILER_BASE_URL);
                            bufferPT.append(youtubeKey);
                        } else {
                            bufferPT.append(TRAILER_BASE_URL);
                            bufferPT.append(youtubeKey);
                            bufferPT.append(SEPARATOR);
                        }

                    }
                    output = bufferPT.toString();
                    Log.v(LOG_TAG, "Parsed: "+output);

                }


            }catch(JSONException e){
                Log.e(LOG_TAG,"unable to parse", e);
            }
            return output;

        }

        private String parseReviewJson(String reviewJson) throws JSONException{
            String outputR= "";
            try {
                if (reviewJson == null) {
                    return "none";
                } else {
                    StringBuffer bufferR = new StringBuffer();
                    final String REVIEW_LIST = "results";
                    final String AUTHOR = "author";
                    final String CONTENT = "content";
                    final String SAYS = " says: \n";
                    final String SEPARATOR = "\n\n\n";
                    JSONObject result = new JSONObject(reviewJson);
                    JSONArray list = result.getJSONArray(REVIEW_LIST);
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject review = list.getJSONObject(i);
                        String author = review.getString(AUTHOR);
                        String content = review.getString(CONTENT);

                        bufferR.append(author);
                        bufferR.append(SAYS);
                        bufferR.append(content);
                        bufferR.append(SEPARATOR);


                    }
                    outputR = bufferR.toString();
                    Log.v(LOG_TAG, outputR);

                }
            }catch (JSONException e) {
                Log.e(LOG_TAG, "unable to parse", e);
            }
            return outputR;
        }


    }//end of FetchDetailsTask





}//end of DetailsFragment


