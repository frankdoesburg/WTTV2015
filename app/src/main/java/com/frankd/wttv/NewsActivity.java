package com.frankd.wttv;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

/**
 * Created by FrankD on 5-6-2015.
 */
public class NewsActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_layout);

        ListView listView = (ListView) findViewById(R.id.listView);

    }

}
