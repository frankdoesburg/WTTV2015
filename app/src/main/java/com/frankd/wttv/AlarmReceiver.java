package com.frankd.wttv;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;


/**
 * Created by FrankD on 7-7-2015.
 */

//receives alarm intents and shows a notification
public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";
    private final String INTENT_ACTION = "INTENT_ACTION";
    public static final String NOTIFICATION_INFO = "NOTIFICATION_INFO"; //used in AlarmReceiver class

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG,"Received intent");

        if(intent.getAction().equals(INTENT_ACTION)) {
            Intent newIntent = new Intent(context, MainActivity.class);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pIntent = PendingIntent.getActivity(context, 0, newIntent, 0);

            String notificationText = "";

            try {
                notificationText = intent.getExtras().getString(NOTIFICATION_INFO);

            } catch (NullPointerException ex) {
                Log.v(TAG, "could not produce notification text");
                notificationText = context.getString(R.string.notification_backup_text);
            }
            Log.v(TAG, "notification text is " + notificationText);

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(getNotificationIcon())
                            .setContentTitle(context.getString(R.string.notification_title))
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(notificationText))//expanded text
                            .setContentText(notificationText)  //small text
                            .setContentIntent(pIntent)
                            .setDefaults(NotificationCompat.DEFAULT_ALL) //vibrate, sound and blinking light
                            .setAutoCancel(true);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

            notificationManager.notify(0, mBuilder.build());

        }
    }

    //set transparent logo for android lollipop
    private int getNotificationIcon() {
        boolean whiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return whiteIcon ? R.drawable.wttv_logo_trans : R.drawable.wttv_logo;
    }
}
