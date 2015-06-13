package com.frankd.wttv;

import android.app.ActionBar;
import android.app.Activity;
import android.database.SQLException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.widget.GridView;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by FrankD on 5-6-2015.
 */
public class ArtistListActivity extends Activity {
    private static final String TAG = "ArtistListActivity";
    private ArrayList<Artist> artistList;

    @Override
       protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artist_list_layout);

        //allow backward navigation to parent activity via actionbar
        getActionBar().setDisplayHomeAsUpEnabled(true);

        //set custom font to action bar
        SpannableString s = new SpannableString(getString(R.string.acts));
        s.setSpan(new TypefaceSpan(this, "big_noodle_titling.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance
        ActionBar actionBar = getActionBar();
        actionBar.setTitle(s);

        //get artist list from database
        artistList = getArtistsFromDB();
        /*
        for(Artist artist: artistList){
            Log.v(TAG, "loaded artist from DB: " + artist.getID() + " name: " + artist.getName());
        }
        */

        //set images for each artist
        setArtistImagesResourceID();

        GridView artistGrid = (GridView) findViewById(R.id.gridView);
        ArtistListAdapter adapter = new ArtistListAdapter(this,artistList);
        artistGrid.setAdapter(adapter);

    }

    //finds images in drawable folder and gets their resource ID in the artist object
    public void setArtistImagesResourceID(){
        for(Artist artist:artistList){
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

    public ArrayList<Artist> getArtistsFromDB(){
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
        return myDbHelper.getAllArtistsFromDB();
    }


}
