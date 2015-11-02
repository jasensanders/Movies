package com.example.jasensanders.movies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View detailView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("M_TITLE")) {
            String Mtitle = intent.getStringExtra("M_TITLE");
            ((TextView) detailView.findViewById(R.id.movieTitle))
                    .setText(Mtitle);
            String Mposter = intent.getStringExtra("M_POSTERURL");
            ImageView poster = (ImageView) detailView.findViewById(R.id.posterView);
            Picasso
                    .with(getActivity())
                    .load(Mposter)
                    .fit()
                    .into(poster);
            String Mrelease = intent.getStringExtra("M_DATE");
            ((TextView) detailView.findViewById(R.id.releaseDate))
                    .setText("Original Release Date: " +Mrelease);
            String Mrating = intent.getStringExtra("M_RATING");
            ((TextView) detailView.findViewById(R.id.rating))
                    .setText("User Rating: " +Mrating);
            String Msynopsis = intent.getStringExtra("M_SYNOPSIS");
            ((TextView) detailView.findViewById(R.id.synopsis))
                    .setText("Overview: \n" + Msynopsis);
        }


        return detailView;
    }

}
