package com.frankd.wttv;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by FrankD on 5-6-2015.
 */
public class ArtistDetailActivity extends Activity {
    private static final String TAG = "ArtistDetailActivity";
    public static final String NOTIFICATION_INFO = "NOTIFICATION_INFO"; //used in AlarmReceiver class
    public static final String INTENT_ACTION = "INTENT_ACTION"; //used in AlarmReceiver class

    private Artist artist;
    private ImageButton favoriteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artist_detail_layout);

        //allow backward navigation to parent activity via actionbar
        getActionBar().setDisplayHomeAsUpEnabled(true);

        //set custom font to action bar
        SpannableString s = new SpannableString(getString(R.string.act));
        s.setSpan(new TypefaceSpan(this, "big_noodle_titling.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance
        ActionBar actionBar = getActionBar();
        actionBar.setTitle(s);

        //get textview
        TextView artistNameTV = (TextView) findViewById(R.id.artistNameTV);
        TextView descriptionTV = (TextView) findViewById(R.id.descriptionTV);
        TextView timeDayTV = (TextView) findViewById(R.id.timeDayTV);
        TextView linkView = (TextView) findViewById(R.id.videoLink);


        //get screen width and size the imageview accordingly
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float width = displaymetrics.widthPixels; //screen width
        float imageHeight = (width/3)*2; //height of the image must me 2/3 of the width
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams((int)width,(int)imageHeight);
        imageView.setLayoutParams(parms);

        //get top layout that contains textviews and imageview
        RelativeLayout topPanel = (RelativeLayout) findViewById(R.id.topPanel);
        //get button
        favoriteButton = (ImageButton) findViewById(R.id.favoriteButton);
        //set button and top panel invisible initially
        favoriteButton.setVisibility(View.INVISIBLE);
        topPanel.setVisibility(View.INVISIBLE);

        //get artist ID from intent
        try{
            Intent intent = getIntent();
            int ID = intent.getIntExtra("ID",-1);
            this.artist = getArtistsFromDB(ID);

            artistNameTV.setText(artist.getName());
            SimpleDateFormat time = new SimpleDateFormat("HH:mm");
            String startTime = "";
            if(artist.getStartTime() != null) {
                startTime = time.format(artist.getStartTime());
            }else{
                Log.v(TAG,"artist " + artist.getId() + " has no starttime!");
            }
            String location = " @ ";
            if(artist.getLocation() != "") {
                 location = location.concat(artist.getLocation());

            }
            timeDayTV.setText(artist.getDay() + " " + startTime + location);
            descriptionTV.setText(artist.getDescription());
            linkView.setText(artist.getYoutubeLink());

            //set heart icon based on artist being favorite
            setHeartIcon(artist.isFavorite());


        }catch (Exception E){
            Log.v(TAG, "could not find artist with in database");
        }

        try{
            imageView.setImageBitmap(getImageFromBase64Blob(artist.getLargeImageBlob()));
        }catch(Exception E){
            Log.v(TAG, "could not load artist image");
        }

        //animation
        final Animation btnAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_right2left);
        Animation titleAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_down);

        //setTimerBtn.startAnimation(btnAnimation);
        titleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                favoriteButton.setVisibility(View.VISIBLE);
                favoriteButton.startAnimation(btnAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        topPanel.setVisibility(View.VISIBLE);
        topPanel.startAnimation(titleAnimation);

        favoriteButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    toggleFavorite();
                }
                return false;
            }
        });

    }

    @Override
    public void onResume(){
        //set heart icon based on artist being favorite
        setHeartIcon(artist.isFavorite());

        super.onResume();
    }

    //set heart icon and update artist favorite status
    public void toggleFavorite(){


        if(artist.isFavorite()){
            //un-favorite arist
            //locally
            artist.setFavorite(false);
            //update heart icon
            setHeartIcon(artist.isFavorite());

            //in database and then in central arraylist
            setFavorite(artist.getId(), false);

        }else{
            //make artist favorite
            //locally
            artist.setFavorite(true);
            //update heart icon
            setHeartIcon(artist.isFavorite());

            //and in database and then in central arraylist
             setFavorite(artist.getId(), true);
        }

    }

    //sets the artist favorite status in DB and in central arrayList
    public void setFavorite(int ID, boolean favorite){
        MainApplication application = (MainApplication)getApplication();
        DataBaseHelper myDbHelper = application.getDatabaseHelper();

        //overwrite existing artist in DB
        myDbHelper.insertArtist(artist);
        //set favorite in central arraylist
        application.setFavorite(ID, favorite);

        //schedule or cancel alarm
        if(favorite){
            setAlarm();
        }else{
            cancelAlarm();
        }
    }

    //sets reminder notification using AlarmManager for current artist
    public void setAlarm(){
        Log.v(TAG,"setting alarm for " + artist.getName());

        if(artist.getStartTime() != null) {
            Date startTime = artist.getStartTime();

            //Date newDate = new Date(System.currentTimeMillis() + 10*1000); //temp, for testing purposes! //TODO: for testing, remove this line !

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());

            //calendar.setTime(newDate); //TODO: for testing, remove this line !
            calendar.setTime(startTime); // notification time from artist date
            calendar.add(Calendar.MINUTE, -10);//set alarm 10 minutes before starttime

            String time = Integer.toString(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + Integer.toString(calendar.get(Calendar.MINUTE));
            Log.v(TAG,"Alarm Time: month " + Integer.toString(calendar.get(Calendar.MONTH)) + " day " + Integer.toString(calendar.get(Calendar.DATE)) + " hour of day " + Integer.toString(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + Integer.toString(calendar.get(Calendar.MINUTE)));

            String stageName = artist.getLocation();

            long when = calendar.getTimeInMillis();
            Intent intent = new Intent(this, AlarmReceiver.class);
            intent.setAction(INTENT_ACTION);
            intent.putExtra(ArtistDetailActivity.NOTIFICATION_INFO, artist.getName() + " " + getString(R.string.starts_at) + " " + time + " " + getString(R.string.at_stage) + " " + stageName);

            PendingIntent pi = PendingIntent.getBroadcast(this, artist.getId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);

            alarmManager.set(AlarmManager.RTC_WAKEUP, when, pi);

            //toast message
            Toast.makeText(this, getString(R.string.reminder_on), Toast.LENGTH_SHORT).show();
        }else{
            Log.v(TAG,"could not set alarm for " + artist.getName() + " as it has no starttime");
        }
    }

    //cancels reminder notification using AlarmManager for current artist
    public void cancelAlarm(){
        //create a similar intent with the artist ID as unique identifier so we can cancel the existing intent.
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.setAction(INTENT_ACTION);
        PendingIntent pi = PendingIntent.getBroadcast(this, artist.getId(),intent,PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(this.ALARM_SERVICE);
        alarmManager.cancel(pi);

        Log.v(TAG,"alarm cancelled for artist " + artist.getName());
    }

    public void setHeartIcon(boolean isFavorite){

        if(artist.isFavorite()){
            favoriteButton.setImageResource(R.drawable.heart_closed);
        }else{
            favoriteButton.setImageResource(R.drawable.heart_open);
        }
    }



    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    public Artist getArtistsFromDB(int ID){
        MainApplication application = (MainApplication)getApplication();
        DataBaseHelper myDbHelper = application.getDatabaseHelper();
        return myDbHelper.getArtistFromDB(ID);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public Bitmap getImageFromBase64Blob(byte[] blob) {
        byte[] decodedString = Base64.decode(blob, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }


}
