package com.frankd.wttv;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by FrankD on 7-7-2015.
 */
//used to set all alarms which are lost after a reboot
public class  AlarmSetter extends BroadcastReceiver {
    private static final String TAG = "AlarmSetter";
    private final String INTENT_ACTION = "INTENT_ACTION";

    @Override
    public void onReceive(Context context, Intent intent) {
        //set alarms for favorite artists
        Log.v(TAG,"AlarmSetter active after device reboot or app update");
        ArrayList<Artist> favorites = findFavoritesInDB(context);
        setAlarmsForFavorites(context,favorites);
    }

    public void setAlarmsForFavorites(Context context, ArrayList<Artist> favorites){
        if(favorites != null){
            Log.v(TAG, "setting " + favorites.size() + " alarms for favorite arists");
            for(Artist artist : favorites){

                Log.v(TAG, "setting alarm for " + artist.getName());

                Date startTime = artist.getStartTime();

                if(startTime != null && isInFuture(startTime)) {
                    Log.v(TAG,"artist " + artist.getName() + " starttime is in the future. Setting alarm");
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    Calendar calendar = Calendar.getInstance();

                    calendar.setTime(startTime); // notification time from artist date
                    String time = Integer.toString(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + Integer.toString(calendar.get(Calendar.MINUTE));
                    String stageName = artist.getLocation();
                    calendar.add(Calendar.MINUTE, -10);//set alarm 10 minutes before starttime

                    Log.v(TAG, "Alarm Time year: " + Integer.toString(calendar.get(Calendar.YEAR)) + " month " + Integer.toString(calendar.get(Calendar.MONTH)) + " day " + Integer.toString(calendar.get(Calendar.DATE)) + " hour of day " + Integer.toString(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + Integer.toString(calendar.get(Calendar.MINUTE)));

                    long when = calendar.getTimeInMillis();
                    Intent intent = new Intent(context, AlarmReceiver.class);
                    intent.setAction(INTENT_ACTION);
                    intent.putExtra(ArtistDetailActivity.NOTIFICATION_INFO, artist.getName() + " " + context.getString(R.string.starts_at) + " " + time + " " + context.getString(R.string.at_stage) + " " + stageName);

                    PendingIntent pi = PendingIntent.getBroadcast(context, artist.getId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);

                    alarmManager.set(AlarmManager.RTC_WAKEUP, when, pi);
                }else{
                    Log.v(TAG,"artist " + artist.getName() + " starttime is in the past. Not setting alarm");
                }
            }
        }
    }

    public boolean isInFuture(Date date){
        Date curDate = new Date(System.currentTimeMillis());

        if(date.compareTo(curDate) == 0){ //date is the same as current time
            return false;
        }else if(date.compareTo(curDate) == -1){ //date is in the past
            return false;
        }else { //date is in the future
            return true;
        }
    }

    public ArrayList<Artist> findFavoritesInDB(Context context){
        DataBaseHelper mDbHelper = new DataBaseHelper(context);

        try {

            mDbHelper.createDataBase();

        } catch (IOException ioe) {

            throw new Error("Unable to create database");

        }

        try {

            mDbHelper.openDataBase();

        }catch(SQLException sqle){

            throw sqle;

        }

        mDbHelper.close();
        return mDbHelper.getFavoritesFromDB();
    }
}
