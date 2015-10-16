package com.frankd.wttv;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Tom on 28/6/15.
 */
public class NewsDetailActivity extends Activity {
    private static final String TAG = "NewsDetailActivity";
    private News news;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_detail_layout);
        context = this;

        //allow backward navigation to parent activity via actionbar
        getActionBar().setDisplayHomeAsUpEnabled(true);

        //set custom font to action bar
        SpannableString s = new SpannableString(getString(R.string.news));
        s.setSpan(new TypefaceSpan(this, "big_noodle_titling.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance
        ActionBar actionBar = getActionBar();
        actionBar.setTitle(s);

        TextView title = (TextView) findViewById(R.id.newsDetailTitle);
        TextView teaser = (TextView) findViewById(R.id.newsDetailTeaser);
        ImageView image = (ImageView) findViewById(R.id.newsDetailImageView);
        TextView body = (TextView) findViewById(R.id.newsDetailBody);
        TextView link = (TextView) findViewById(R.id.videoLink);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ID = news.getId();
                Intent intent = new Intent(context, NewsImageActivity.class);
                intent.putExtra("ID", ID);
                context.startActivity(intent);
            }
        });

        try{
            Intent intent = getIntent();
            int ID = intent.getIntExtra("ID",-1);
            this.news = getNewsFromDb(ID);

            title.setText(news.getTitle());
            teaser.setText(news.getTeaser());
            body.setText(news.getBody());
            link.setText(news.getVideo());


        }catch (Exception E){
            Log.v(TAG, "could not find artist with in database");
        }

        try{
            image.setImageBitmap(getImageFromBase64Blob(news.getLargeImageBlob()));
        }catch(Exception E){
            Log.v(TAG, "could not load artist image");
        }

    }

    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public News getNewsFromDb(int id) {
        MainApplication application = (MainApplication)getApplication();
        DataBaseHelper myDbHelper = application.getDatabaseHelper();
        return myDbHelper.getNewsFromDB(id);
    }

    public Bitmap getImageFromBase64Blob(byte[] blob) {
        byte[] decodedString = Base64.decode(blob, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }
}
