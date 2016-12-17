package com.nealgosalia.timetable.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
        c.set(Calendar.SECOND,0);
        c.setTimeInMillis(c.getTimeInMillis()+300000);
        String time=String.format(Locale.US, "%02d:%02d",c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE));
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Next lecture")
                .setContentText("at "+time)
                .setColor(Color.argb(255,67,133,244));
        mBuilder.setContentIntent(pi);
        mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();
        mNotificationManager.notify(0, mBuilder.build());
    }
}
