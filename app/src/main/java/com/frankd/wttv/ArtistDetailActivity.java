package com.frankd.wttv;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by FrankD on 5-6-2015.
 */
public class ArtistDetailActivity extends Activity {
    private static final String TAG = "ArtistDetailActivity";
    private Artist artist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artist_detail_layout);

        //allow backward navigation to parent activity via actionbar
        getActionBar().setDisplayHomeAsUpEnabled(true);

        //set custom font to action bar
        SpannableString s = new SpannableString(getString(R.string.act));
        s.setSpan(new TypefaceSpan(this, "big_noodle_titling.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance
        ActionBar actionBar = getActionBar();
        actionBar.setTitle(s);

        //get textview
        CustomFontTextView artistNameTV = (CustomFontTextView) findViewById(R.id.artistNameTV);
        CustomFontTextView descriptionTV = (CustomFontTextView) findViewById(R.id.descriptionTV);
        CustomFontTextView timeDayTV = (CustomFontTextView) findViewById(R.id.timeDayTV);


        //get screen width and size the imageview accordingly
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float width = displaymetrics.widthPixels; //screen width
        float imageHeight = (width/3)*2; //height of the image must me 2/3 of the width
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams((int)width,(int)imageHeight);
        imageView.setLayoutParams(parms);

        //get top layout that contains textviews and imageview
        RelativeLayout topPanel = (RelativeLayout) findViewById(R.id.topPanel);
        //get button
        final ImageButton favoriteButton = (ImageButton) findViewById(R.id.favoriteButton);
        //set button and top panel invisible initially
        favoriteButton.setVisibility(View.INVISIBLE);
        topPanel.setVisibility(View.INVISIBLE);

        //get artist ID from intent
        try{
            Intent intent = getIntent();
            int ID = intent.getIntExtra("ID",-1);
            this.artist = getArtistsFromDB(ID);

            artistNameTV.setText(artist.getName());
            timeDayTV.setText(artist.getDay() + " " + artist.getStartTime() + " @" + artist.getLocation());
            descriptionTV.setText(artist.getDescription());

        }catch (Exception E){
            Log.v(TAG, "could not find artist with in database");
        }

        try{
             imageView.setImageBitmap(artist.getLargeImage());
        }catch(Exception E){
            Log.v(TAG, "could not load artist image");
        }

        //animation
        final Animation btnAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_right2left);
        Animation titleAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_down);

        //setTimerBtn.startAnimation(btnAnimation);
        titleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                favoriteButton.setVisibility(View.VISIBLE);
                favoriteButton.startAnimation(btnAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        topPanel.setVisibility(View.VISIBLE);
        topPanel.startAnimation(titleAnimation);


    }


    public Artist getArtistsFromDB(int ID){
        DataBaseHelper myDbHelper = new DataBaseHelper(this);
        myDbHelper = new DataBaseHelper(this);

        try {

            myDbHelper.createDataBase();

        } catch (IOException ioe) {

            throw new Error("Unable to create database");

        }

        try {

            myDbHelper.openDataBase();

        }catch(SQLException sqle){

            throw sqle;

        }

        myDbHelper.close();
        return myDbHelper.getArtistFromDB(ID);
    }

    //finds images in drawable folder and gets their resource ID in the artist object
    public void setArtistImagesResourceID(){
            int ID = artist.getID();

            try{
                String uri = "act" + ID; //creates filename dynamically, e.g. act3 or act4 (skip the .jpg or .png suffix)
                int imageID = getResources().getIdentifier(uri,"drawable", getPackageName());

                // Log.v(TAG,"created image ID : " + imageID + " for " + uri);
                artist.setThumbnailImageId(imageID);

            }catch(NullPointerException E){
                Log.v(TAG,"could not load image with ID " + ID + " and acts name " + artist.getName());
            }

            try{
                String uri = "act" + ID + "_large"; //creates filename dynamically, e.g. act3 or act4 (skip the .jpg or .png suffix)
                int imageID = getResources().getIdentifier(uri,"drawable", getPackageName());

                // Log.v(TAG,"created image ID : " + imageID + " for " + uri);
                artist.setLargeImageID(imageID);

            }catch(NullPointerException E){
                Log.v(TAG,"could not load image with ID " + ID + " and acts name " + artist.getName());
            }

        }

}
