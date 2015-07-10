package com.frankd.wttv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Created by FrankD on 5-7-2015.
 */
public class FavoritesListAdapter extends ArrayAdapter {
    private static final String TAG = "FavoritesListAdapter";
    private Context context;
    private ArrayList<Artist> favorites;
    private ImageLoader imageLoader = ImageLoader.getInstance();


    public FavoritesListAdapter(Context context, ArrayList<Artist> favorites) {
        super(context,R.layout.favorites_list_item,favorites);
        this.context = context;
        this.favorites = favorites;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.favorites_list_item, parent, false);
        }

        //get artist info
        Artist curArtist = favorites.get(position);

        //set imageview and scale the image making the width 50% of the screen size
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);

//        imageView.setImageBitmap(getImageFromBase64Blob(curArtist.getThumbnailImageBlob()));
        imageLoader.displayImage("db://" + curArtist.getId(), imageView);


        TextView artistNameTV = (TextView) convertView.findViewById(R.id.artistNameTV);
        TextView timeDayTV = (TextView) convertView.findViewById(R.id.timeDayTV);

        artistNameTV.setText(curArtist.getName());
        SimpleDateFormat time = new SimpleDateFormat("HH:mm");
        String startTime = "";
        if(curArtist.getStartTime() != null) {
            startTime = time.format(curArtist.getStartTime());
        }
        String location = " @ ";
        if(curArtist.getLocation() != "") {
            location = location.concat(curArtist.getLocation());

        }
        timeDayTV.setText(curArtist.getDay() + " " + startTime + location);

        convertView.setId(curArtist.getId());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ID = v.getId();
                Intent intent = new Intent(context.getApplicationContext(), ArtistDetailActivity.class);
                intent.putExtra("ID", ID);
                context.startActivity(intent);
            }
        });

        RelativeLayout baseLayout = (RelativeLayout) convertView.findViewById(R.id.baseLayout);
        RelativeLayout overlay = (RelativeLayout) convertView.findViewById(R.id.overlay);

        //add overlay if act is in the past
        if(actStarttimeInPast(curArtist)){
            baseLayout.setBackgroundResource(0);
            overlay.setVisibility(View.VISIBLE);
        }else{
            baseLayout.setBackgroundResource(R.color.whiteTransparent);
            overlay.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    //returns true if starttime of act is in the past
    public boolean actStarttimeInPast(Artist artist){
        Date curdate = new Date(System.currentTimeMillis());

        Date artistDate = artist.getStartTime();

        if(artistDate == null){
            return false;
        }
        //dates are equal
        if(artistDate.compareTo(curdate)==0){
            return false;
            //artist startdate is before curdate (act is in the past)
        }else if(artistDate.compareTo(curdate)==-1){
            return true;
        }else{
            return false;
        }
    }

    public Bitmap getImageFromBase64Blob(byte[] blob){
        byte[] decodedString = Base64.decode(blob, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

}
