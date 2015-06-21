package com.frankd.wttv;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.SQLException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by FrankD on 5-6-2015.
 */
public class ArtistListActivity extends Activity {
    private static final String TAG = "ArtistListActivity";
    private ArrayList<Artist> artistList;

    //layout of navigation drawer
    private DrawerLayout mDrawerLayout;
    //action bar toggle
    private ActionBarDrawerToggle mDrawerToggle;

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

        initMenuDrawer();

    }

    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
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

    public void initMenuDrawer(){
        //navigation drawer setup
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //action bar toggle
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //getActionBar().setTitle(mTitle);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // getActionBar().setTitle(mDrawerTitle);
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        //initialize ListView
        LinearLayout news = (LinearLayout) findViewById(R.id.news);
        LinearLayout artists = (LinearLayout) findViewById(R.id.artists);
        LinearLayout timetable = (LinearLayout) findViewById(R.id.timetable);
        LinearLayout map = (LinearLayout) findViewById(R.id.map);
        LinearLayout favorites = (LinearLayout) findViewById(R.id.favorites);

        //set the current activity as selected
        artists.setSelected(true);

        news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });

        artists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getApplicationContext(),ArtistListActivity.class);
                //startActivity(intent);
                mDrawerLayout.closeDrawers();
            }
        });

        timetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),TimetableActivity.class);
                startActivity(intent);
            }
        });

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MapActivity.class);
                startActivity(intent);
            }
        });

        favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),FavoritesActivity.class);
                startActivity(intent);
            }
        });

        //set size for imageview with festival logo
        //get screen width and size the imageview accordingly
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float width = displaymetrics.widthPixels; //screen width
        ImageView logoImageView = (ImageView) findViewById(R.id.logoImageView);
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams((int)width/2,(int)width/2);
        logoImageView.setLayoutParams(parms);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

}
