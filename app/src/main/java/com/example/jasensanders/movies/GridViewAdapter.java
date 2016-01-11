package com.example.jasensanders.movies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Jasen Sanders on 016,12/16/15.
 */
public class GridViewAdapter extends ArrayAdapter<Movie> {
        private Context context;
        private LayoutInflater inflater;
        private ArrayList<Movie> moviedata;
        private final String LOG_TAG = GridViewAdapter.class.getSimpleName();

        public GridViewAdapter(Context context, ArrayList<Movie> data) {
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


