package com.frankd.wttv;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.ImageViewState;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

public class NewsImageActivity extends Activity {
    private static final String TAG = "NewsImageActivity";

    private News news;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_image);

        //set custom font to action bar
        SpannableString s = new SpannableString(getString(R.string.newsImage));
        s.setSpan(new TypefaceSpan(this, "big_noodle_titling.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance
        ActionBar actionBar = getActionBar();
        actionBar.setTitle(s);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //set timetable image
        SubsamplingScaleImageView imgView = (SubsamplingScaleImageView)findViewById(R.id.imageView);
        imgView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP);
        imgView.setMinimumDpi(100);
        imgView.setMaximumDpi(600);
        imgView.setDoubleTapZoomDpi(200);
        imgView.setMinimumTileDpi(100);

        try{
            Intent intent = getIntent();
            int ID = intent.getIntExtra("ID",-1);
            this.news = getNewsFromDb(ID);

        }catch (Exception E){
            Log.v(TAG, "could not find artist with in database");
        }

        try{
            imgView.setImage(ImageSource.bitmap(getImageFromBase64Blob(news.getLargeImageBlob())), new ImageViewState(0, new PointF(0, 0), SubsamplingScaleImageView.ORIENTATION_0));

        }catch(Exception E){
            Log.v(TAG, "could not load artist image");
        }



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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }
}
