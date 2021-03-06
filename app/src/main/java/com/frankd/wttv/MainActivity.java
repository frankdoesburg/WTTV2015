package com.frankd.wttv;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.SQLException;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import uk.co.chrisjenx.calligraphy.CalligraphyTypefaceSpan;
import uk.co.chrisjenx.calligraphy.TypefaceUtils;


public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    //layout of navigation drawer
    private DrawerLayout mDrawerLayout;
    //action bar toggle
    private ActionBarDrawerToggle mDrawerToggle;
    private MainListAdapter listAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MainApplication application;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        //set custom font to action bar
        SpannableString s = new SpannableString(getString(R.string.main_activity));
        CalligraphyTypefaceSpan typefaceSpan = new CalligraphyTypefaceSpan(TypefaceUtils.load(getAssets(), "fonts/big_noodle_titling.ttf"));
        s.setSpan(typefaceSpan, 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance
        ActionBar actionBar = getActionBar();
        actionBar.setTitle(s);

        //initialize news feed and listview
        listView = (ListView) findViewById(R.id.listView);
        application = (MainApplication)getApplication();

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.grid_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                application.refreshNews();
            }

        });

        application.setRefreshDataListener(new MainApplication.RefreshDataListener() {
            @Override
            public void onQueueEmpty() {
                application = (MainApplication)getApplication();
                ArrayList<News> refreshedNews = new ArrayList<>(application.getNewsList());
                listAdapter.refresh(refreshedNews);
                swipeRefreshLayout.setRefreshing(false);
            }

            public void onQueueNotEmpty() {
                if(!swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(true);
                        }
                    });
                }
            }
        });

        ArrayList<News> news = application.getNewsList();
        listAdapter = new MainListAdapter(this, news);
        listView.setAdapter(listAdapter);
        initMenuDrawer();

        SharedPreferences sharedPreferences = getSharedPreferences("PREFS", 0);
        if(sharedPreferences.getBoolean("firstStartUpNews", true)) {
            Toast.makeText(this, "Pull to refresh nieuws", Toast.LENGTH_LONG).show();
            sharedPreferences.edit().putBoolean("firstStartUpNews", false).commit();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
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
        news.setSelected(true);

        news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                //startActivity(intent);
                mDrawerLayout.closeDrawers();
            }
        });

        artists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ArtistListActivity.class);
                startActivity(intent);
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
