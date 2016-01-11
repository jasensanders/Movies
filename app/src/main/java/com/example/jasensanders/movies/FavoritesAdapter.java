package com.example.jasensanders.movies;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.example.jasensanders.movies.data.MovieFavContract;
import com.squareup.picasso.Picasso;

/**
 * Created by Jasen Sanders on 016,12/16/15.
 */
public class FavoritesAdapter extends CursorAdapter{


    public FavoritesAdapter(Context context, Cursor c, int flags){super(context, c, flags);}



    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_tile, parent, false);

        return view;



    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        String url = cursor.getString(MovieFavContract.COL_THUMB);
        Picasso
                .with(context)
                .load(url)
                .error(R.mipmap.ic_launcher)
                .fit() // will explain later
                .into((ImageView) view);

    }
}
