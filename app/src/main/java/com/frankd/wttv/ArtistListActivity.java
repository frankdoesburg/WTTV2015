package com.frankd.wttv;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.ArrayList;

import info.hoang8f.android.segmented.SegmentedGroup;
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
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArtistListAdapter adapter;
    private GridView artistGrid;
    private MainApplication application;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
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
        application = (MainApplication) getApplication();
        artistList = application.getArtists();

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.grid_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                application.refreshArtists();
            }
        });
        SegmentedGroup segmentedGroup = (SegmentedGroup) findViewById(R.id.segmented_control);
        segmentedGroup.setTintColor(Color.parseColor("#333333"), Color.parseColor("#ffffff"));

        application.setRefreshDataListener(new MainApplication.RefreshDataListener() {
            @Override
            public void onQueueEmpty() {
                ArrayList<Artist> newArtists = new ArrayList<>(application.getArtists());

                adapter.refresh(newArtists);
                swipeRefreshLayout.setRefreshing(false);
            }

            public void onQueueNotEmpty() {
                if(!swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(true);
                }
            }
        });

        artistGrid = (GridView) findViewById(R.id.gridView);
        adapter = new ArtistListAdapter(this, artistList);
        artistGrid.setAdapter(adapter);

        initMenuDrawer();

        SharedPreferences sharedPreferences = getSharedPreferences("PREFS", 0);
        if(sharedPreferences.getBoolean("firstStartUpActs", true)) {
            Toast.makeText(this, "Pull to refresh acts", Toast.LENGTH_LONG).show();
            sharedPreferences.edit().putBoolean("firstStartUpActs", false).commit();
        }

    }


    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.segment_btn_all:
                if (checked) {
                    adapter.getFilter().filter("");
                    artistGrid.setSelection(0);
                }
                break;
            case R.id.segment_btn_vrij:
                if (checked) {
                    adapter.getFilter().filter("vrijdag");
                    artistGrid.setSelection(0);
                }
                break;
            case R.id.segment_btn_zat:
                if (checked) {
                    adapter.getFilter().filter("zaterdag");
                    artistGrid.setSelection(0);
                }
                break;
            case R.id.segment_btn_zon:
                if (checked) {
                    adapter.getFilter().filter("zondag");
                    artistGrid.setSelection(0);
                }

                break;
        }
    }

    public void initMenuDrawer() {
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
        mDrawerToggle.setDrawerIndicatorEnabled(true);

        //initialize ListView
        LinearLayout news = (LinearLayout) findViewById(R.id.news);
        LinearLayout artists = (LinearLayout) findViewById(R.id.artists);
        LinearLayout timetable = (LinearLayout) findViewById(R.id.timetable);
        LinearLayout map = (LinearLayout) findViewById(R.id.map);
        LinearLayout favorites = (LinearLayout) findViewById(R.id.favorites);
        LinearLayout appothekers = (LinearLayout) findViewById(R.id.appothekers);

        //set the current activity as selected
        artists.setSelected(true);

        news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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
                Intent intent = new Intent(getApplicationContext(), TimetableActivity.class);
                startActivity(intent);
            }
        });

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(intent);
            }
        });

        favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FavoritesActivity.class);
                startActivity(intent);
            }
        });

        appothekers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://deappothekers.nl/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });


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
