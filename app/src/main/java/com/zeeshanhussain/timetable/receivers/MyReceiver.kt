package com.zeeshanhussain.timetable.receivers

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color

import com.zeeshanhussain.timetable.R
import com.zeeshanhussain.timetable.ui.activity.MainActivity

import androidx.core.app.NotificationCompat

/**
 * Created by kira on 16/12/16.
 */

class MyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val subjectName = intent.extras!!.getString("SUBJECT_NAME")
        val startTime = intent.extras!!.getString("START_TIME")
        showNotification(context, subjectName, startTime)
    }

    private fun showNotification(context: Context, subjectName: String?, startTime: String?) {
        val intent = Intent(context, MainActivity::class.java)
        val pi = PendingIntent.getActivity(context, 0, intent, 0)
        val mBuilder = NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(subjectName)
                .setContentText("at " + startTime!!)
                .setColor(Color.argb(255, 67, 133, 244))
        mBuilder.setContentIntent(pi)
        mBuilder.setDefaults(Notification.DEFAULT_VIBRATE)
        mBuilder.setAutoCancel(true)
        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.cancelAll()
        mNotificationManager.notify(0, mBuilder.build())
    }
}
