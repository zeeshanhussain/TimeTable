package com.zeeshanhussain.timetable.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import androidx.core.app.NotificationCompat;

import com.zeeshanhussain.timetable.ui.activity.MainActivity;
import com.zeeshanhussain.timetable.R;

/**
 * Created by kira on 16/12/16.
 */

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String subjectName = intent.getExtras().getString("SUBJECT_NAME");
        String startTime = intent.getExtras().getString("START_TIME");
        showNotification(context, subjectName, startTime);
    }

    public void showNotification(Context context, String subjectName, String startTime) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(subjectName)
                .setContentText("at " + startTime)
                .setColor(Color.argb(255, 67, 133, 244));
        mBuilder.setContentIntent(pi);
        mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();
        mNotificationManager.notify(0, mBuilder.build());
    }
}
