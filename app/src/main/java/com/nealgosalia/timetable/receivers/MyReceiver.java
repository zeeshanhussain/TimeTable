package com.nealgosalia.timetable.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.nealgosalia.timetable.R;
import com.nealgosalia.timetable.activities.TodayActivity;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by kira on 16/12/16.
 */

public class MyReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        showNotification(context);
    }

    public void showNotification(Context context) {
        Intent intent = new Intent(context, TodayActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
        Calendar c = Calendar.getInstance();
        int hour=c.get(Calendar.HOUR_OF_DAY);
        int minute=c.get(Calendar.MINUTE);
        if(minute>54){
            hour++;
            minute = minute -55;
        } else{
            minute = minute + 5;
        }
        String time=String.format(Locale.US, "%02d:%02d",hour,minute);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Next lecture")
                .setContentText("at "+time);
        mBuilder.setContentIntent(pi);
        mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();
        mNotificationManager.notify(0, mBuilder.build());
    }
}
