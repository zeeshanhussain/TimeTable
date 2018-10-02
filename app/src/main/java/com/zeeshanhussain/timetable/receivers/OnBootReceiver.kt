package com.zeeshanhussain.timetable.receivers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log

import com.zeeshanhussain.timetable.database.AppDatabase
import com.zeeshanhussain.timetable.model.Lecture
import com.zeeshanhussain.timetable.utils.AppExecutors

import java.util.ArrayList
import java.util.Calendar
import java.util.Locale

import android.content.Context.ALARM_SERVICE

/**
 * Created by kira on 18/12/16.
 */

class OnBootReceiver : BroadcastReceiver() {
    private val lecturesList = ArrayList<Lecture>()
    private var appDatabase: AppDatabase? = null

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == "com.zeeshanhussain.timetable.NOTIFY") {
            val mSharedPreference = PreferenceManager.getDefaultSharedPreferences(context)
            val notificationTime = Integer.parseInt(mSharedPreference.getString("NOTIFICATION_TIME", "-1")!!)
            if (notificationTime != -1) {
                Log.d(TAG, intent.action)
                appDatabase = AppDatabase.getsInstance(context)
                val calendar = Calendar.getInstance()
                AppExecutors.instance.diskIO().execute {
                    lecturesList.addAll(appDatabase!!.lectureDao().loadAllLectures())
                    if (lecturesList.size != 0) {
                        for (lecture in lecturesList) {
                            calendar.timeInMillis = System.currentTimeMillis()
                            val dayOfWeek = lecture.day + 1 % 7 + 1
                            calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek)
                            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(lecture.startHour.toString()))
                            calendar.set(Calendar.MINUTE, Integer.parseInt(lecture.startMinute.toString()))
                            calendar.set(Calendar.SECOND, 0)
                            calendar.set(Calendar.MILLISECOND, 0)
                            calendar.timeInMillis = calendar.timeInMillis - notificationTime * MINUTE
                            if (System.currentTimeMillis() > calendar.timeInMillis) {
                                calendar.set(Calendar.WEEK_OF_MONTH, calendar.get(Calendar.WEEK_OF_MONTH) + 1)
                            }
                            val i = Intent(context.applicationContext, MyReceiver::class.java)
                            i.putExtra("SUBJECT_NAME", lecture.subjectName)
                            i.putExtra("START_TIME", String.format(Locale.getDefault(), "%02d:%02d", lecture.startHour, lecture.startMinute))
                            val randomRequestCode = calendar.timeInMillis.toInt()
                            val pendingIntent = PendingIntent.getBroadcast(context, randomRequestCode, i, PendingIntent.FLAG_UPDATE_CURRENT)
                            val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
                            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY * 7, pendingIntent)
                            Log.d(TAG, "Notification Time: " + calendar.time.toString() + " " + calendar.timeInMillis.toString())
                        }
                    }
                }

            }
        }
    }

    companion object {

        private val MINUTE = 60000
        private val TAG = "OnBootReceiver"
    }
}