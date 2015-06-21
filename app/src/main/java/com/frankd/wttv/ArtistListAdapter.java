package com.frankd.wttv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by FrankD on 5-6-2015.
 */
public class ArtistListAdapter extends ArrayAdapter {

    private Context context;
    private ArrayList<Artist> artists;

    public ArtistListAdapter(Context context, ArrayList<Artist> artists){
        super(context,R.layout.artist_grid_item,artists);
        this.context = context;
        this.artists = artists;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            //convertView = mInflater.inflate(R.layout.artist_list_item, parent, false);
            convertView = mInflater.inflate(R.layout.artist_grid_item, parent, false);
        }
        //get artist info
        Artist curArtist = artists.get(position);

        //get screen width
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;

        //set imageview and scale the image making the width 50% of the screen size
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
       // imageView.setImageResource(curArtist.getThumbnailImageId());
        imageView.setImageBitmap(curArtist.getThumbnailImage());
        RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(width/2,width/2);
        imageView.setLayoutParams(parms);

        TextView artistNameTV = (TextView) convertView.findViewById(R.id.artistNameTV);
        TextView timeDayTV = (TextView) convertView.findViewById(R.id.timeDayTV);

        artistNameTV.setText(curArtist.getName());
        timeDayTV.setText(curArtist.getDay() + " " + curArtist.getStartTime());

        convertView.setId(curArtist.getID());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ID = v.getId();
                Intent intent = new Intent(context.getApplicationContext(),ArtistDetailActivity.class);
                intent.putExtra("ID",ID);
                context.startActivity(intent);
            }
        });


        return convertView;
    }
}
