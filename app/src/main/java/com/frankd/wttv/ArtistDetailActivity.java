package com.frankd.wttv;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;

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


        //get artist
        try{
            Intent intent = getIntent();
            int ID = intent.getIntExtra("ID",-1);
            this.artist = getArtistsFromDB(ID);

            artistNameTV.setText(artist.getName());
            timeDayTV.setText(artist.getDay() + " " + artist.getTime() + " @" + artist.getStage());
            descriptionTV.setText(artist.getDescription());

        }catch (Exception E){
            Log.v(TAG, "could not find artist with in database");
        }

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

}
