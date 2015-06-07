package com.frankd.wttv;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.widget.ListView;

/**
 * Created by FrankD on 5-6-2015.
 */
public class NewsActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_layout);

        //allow backward navigation to parent activity via actionbar
        getActionBar().setDisplayHomeAsUpEnabled(true);

        //set custom font to action bar
        SpannableString s = new SpannableString(getString(R.string.news));
        s.setSpan(new TypefaceSpan(this, "big_noodle_titling.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance
        ActionBar actionBar = getActionBar();
        actionBar.setTitle(s);

        ListView listView = (ListView) findViewById(R.id.listView);

    }

}
