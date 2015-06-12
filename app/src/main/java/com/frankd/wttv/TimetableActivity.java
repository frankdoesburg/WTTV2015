package com.frankd.wttv;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;

import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

/**
 * Created by FrankD on 5-6-2015.
 */
public class TimetableActivity extends Activity {
    private static final String TAG = "TimetableActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timetable_layout);

        //allow backward navigation to parent activity via actionbar
        getActionBar().setDisplayHomeAsUpEnabled(true);

        //set custom font to action bar
        SpannableString s = new SpannableString(getString(R.string.timetable));
        s.setSpan(new TypefaceSpan(this, "big_noodle_titling.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance
        ActionBar actionBar = getActionBar();
        actionBar.setTitle(s);

        //set timetable image
        TouchImageView imgView = (TouchImageView)findViewById(R.id.imageView);
        imgView.setImageResource(R.drawable.blokkenschema_northsea);

        imgView.scrollToTopLeft();

    }

}
