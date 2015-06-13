package com.frankd.wttv;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set custom font to action bar
        SpannableString s = new SpannableString(getString(R.string.main_activity));
        s.setSpan(new TypefaceSpan(this,"big_noodle_titling.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance
        ActionBar actionBar = getActionBar();
        actionBar.setTitle(s);

        //initialize ListView
        LinearLayout news = (LinearLayout) findViewById(R.id.news);
        LinearLayout artists = (LinearLayout) findViewById(R.id.artists);
        LinearLayout timetable = (LinearLayout) findViewById(R.id.timetable);
        LinearLayout favorites = (LinearLayout) findViewById(R.id.favorites);

        news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),NewsActivity.class);
                startActivity(intent);
            }
        });

        artists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ArtistListActivity.class);
                startActivity(intent);
            }
        });

        timetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),TimetableActivity.class);
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
        float imageHeight = displaymetrics.heightPixels;; //height of the image must me 2/3 of the width
        ImageView logoImageView = (ImageView) findViewById(R.id.logoImageView);
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams((int)width/2,(int)imageHeight/2);
        logoImageView.setLayoutParams(parms);

    }


}
