package com.example.jasensanders.movies;

import android.content.Context;
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

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private ImageAdapter posterLoad;
    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    public MainActivityFragment() {
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        //setContentView(R.layout.fragment_main);

        setHasOptionsMenu(true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragmentmenu, menu);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);




        posterLoad = new ImageAdapter(getActivity(), eatFoodyImages);
        GridView gridview = (GridView) rootView.findViewById(R.id.gridView);
        gridview.setAdapter(posterLoad);
        Log.d(LOG_TAG, "GridViewAdapterSet");

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(getActivity(), "This Works" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });



        return rootView;
    }

    public static String[] eatFoodyImages = {
            "http://i.imgur.com/rFLNqWI.jpg",
            "http://i.imgur.com/C9pBVt7.jpg",
            "http://i.imgur.com/rT5vXE1.jpg",
            "http://i.imgur.com/aIy5R2k.jpg",
            "http://i.imgur.com/MoJs9pT.jpg",
            "http://i.imgur.com/S963yEM.jpg",
            "http://i.imgur.com/rLR2cyc.jpg",
            "http://i.imgur.com/SEPdUIx.jpg",
            "http://i.imgur.com/aC9OjaM.jpg",
            "http://i.imgur.com/76Jfv9b.jpg",
            "http://i.imgur.com/fUX7EIB.jpg",
            "http://i.imgur.com/syELajx.jpg",
            "http://i.imgur.com/COzBnru.jpg",
            "http://i.imgur.com/Z3QjilA.jpg",
    };

    public class ImageAdapter extends ArrayAdapter {
        private Context context;
        private LayoutInflater inflater;
        private String[] imageurls;
        private final String LOG_TAG = ImageAdapter.class.getSimpleName();

        public ImageAdapter(Context context, String[] imageUrls) {
            super(context, R.layout.image_tile, imageUrls);

            this.context = context;
            this.imageurls = imageUrls;

            inflater = LayoutInflater.from(context);
            Log.d(LOG_TAG, "ImageAdapterInstantiated");
        }



        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {

            Log.d(LOG_TAG, "AdapterGetViewCalled");
            if (null == convertView) {
                convertView = inflater.inflate(R.layout.image_tile, parent, false);
            }

            Picasso
                    .with(context)
                    .load(imageurls[position])
                    .error(R.mipmap.ic_launcher)
                    .fit() // will explain later
                    .into((ImageView) convertView);

            return convertView;


        }
    }
}
