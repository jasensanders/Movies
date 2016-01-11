package com.example.jasensanders.movies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        Uri data = intent.getData();
        //If this was not launched by the GridViewAdapter click listener
        if (savedInstanceState == null && data != null &&  Utility.isTablet(this)) {
            //Create a bundle to send the uri data to the new DetailFragment
            Bundle arguments = new Bundle();
            //this detail view is always launched as an intent from MainActivity's
            // ForecastFragment so we pass them into a bundle to pass them to our DetailFragment.
            arguments.putParcelable(DetailActivityFragment.DETAIL_URI, data);
            DetailListFragment fragment = new DetailListFragment();
            fragment.setArguments(arguments);
            //Get a new DetailFragment(fragment) with our uri passed  in as a bundle(Arguments)
            //and dump it into our weather_detail_container
            //in the activity_detail.xml layout.

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment)
                    .commit();
        }else if(savedInstanceState != null && !Utility.isTablet(this) && data != null){
            Intent Dintent = new Intent(this, DetailActivityFragment.class)
                    .setData(data);
            startActivity(Dintent);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
