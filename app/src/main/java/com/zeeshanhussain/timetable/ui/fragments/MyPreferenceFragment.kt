package com.zeeshanhussain.timetable.ui.fragments

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Environment
import android.preference.ListPreference
import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast

import com.zeeshanhussain.timetable.R
import com.zeeshanhussain.timetable.ui.activity.MainActivity
import com.zeeshanhussain.timetable.ui.activity.PreferencesActivity
import com.zeeshanhussain.timetable.utils.Alarms

import java.io.File
import java.util.ArrayList

import android.content.Context.ALARM_SERVICE

/**
 * Created by men_in_black007 on 15/12/16.
 */

class MyPreferenceFragment : PreferenceFragment() {
    private var mActivity: PreferencesActivity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preference)
        mActivity = activity as PreferencesActivity
        val notificationTime = findPreference("notificationTime") as ListPreference
        notificationTime.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, o ->
            val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
            val editor = prefs.edit()
            editor.putString("NOTIFICATION_TIME", o as String)
            editor.apply()
            if (alarmsList.size != 0) {
                for (alarm in alarmsList) {
                    val alarmManager = alarm.context!!.getSystemService(ALARM_SERVICE) as AlarmManager
                    alarmManager.cancel(alarm.pendingIntent)
                }
            }
            if (o != "-1") {
                val i = Intent()
                i.action = "com.zeeshanhussain.timetable.NOTIFY"
                activity.sendBroadcast(i)
                Log.d(TAG, "Broadcasted")
            }
            true
        }
        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        val editor = prefs.edit()
        editor.putString("NOTIFICATION_TIME", notificationTime.value)
        editor.apply()
        val backup = findPreference("backup")
        val restore = findPreference("restore")
        val reset = findPreference("reset")
        backup.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            if (mActivity!!.isStoragePermissionGranted(activity)) {
                val alertDialog = AlertDialog.Builder(activity)
                alertDialog.setTitle(resources.getString(R.string.backup))
                alertDialog.setMessage(resources.getString(R.string.backup_question))
                alertDialog.setPositiveButton(resources.getString(R.string.yes)) { dialog, which ->
                    val backupDBPath = Environment.getExternalStorageDirectory().absolutePath + "/Timetable/"
                    val TimetableDB = File(backupDBPath + "Timetable")
                    //final File lectureDB = new File(backupDBPath + "lecture.db");
                    if (!TimetableDB.exists()) {
                        if (mActivity!!.exportDatabase("Timetable")) {
                            Toast.makeText(activity, resources.getString(R.string.backup_successful), Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(activity, resources.getString(R.string.please_create_timetable), Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val overwriteDialog = AlertDialog.Builder(activity)
                                .setTitle(resources.getString(R.string.warning))
                                .setMessage(resources.getString(R.string.overwrite_backup))
                                .setPositiveButton(resources.getString(R.string.yes)) { dialog, which ->
                                    TimetableDB.delete()
                                    if (mActivity!!.exportDatabase("Timetable")) {
                                        Toast.makeText(activity, resources.getString(R.string.backup_successful), Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(activity, resources.getString(R.string.please_create_timetable), Toast.LENGTH_SHORT).show()
                                    }
                                }
                                .setNegativeButton(resources.getString(R.string.no), null)
                                .create()
                        overwriteDialog.show()
                    }
                }
                alertDialog.setNegativeButton(resources.getString(R.string.no), null)
                alertDialog.show()
            } else {
                Toast.makeText(activity, resources.getString(R.string.no_permission_granted), Toast.LENGTH_SHORT).show()
            }
            false
        }
        restore.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            if (mActivity!!.isStoragePermissionGranted(activity)) {
                val alertDialog = AlertDialog.Builder(activity)
                alertDialog.setTitle(resources.getString(R.string.restore))
                alertDialog.setMessage(resources.getString(R.string.restore_question))
                alertDialog.setPositiveButton(resources.getString(R.string.yes)) { dialog, which ->
                    val backupDBPath = "data/data/com.zeeshanhussain.timetable/databases/"
                    val TimetableDB = File(backupDBPath + "Timetable")
                    //final File lectureDB = new File(backupDBPath + "lecture.db");
                    if (!TimetableDB.exists()) {
                        if (mActivity!!.importDatabase("Timetable")) {
                            restartApplication()
                            Toast.makeText(activity, resources.getString(R.string.restore_successful), Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(activity, resources.getString(R.string.backup_not_found), Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val overwriteDialog = AlertDialog.Builder(activity)
                                .setTitle(resources.getString(R.string.warning))
                                .setMessage(resources.getString(R.string.overwrite_timetable))
                                .setPositiveButton(resources.getString(R.string.yes)) { dialog, which ->
                                    TimetableDB.delete()
                                    if (mActivity!!.importDatabase("Timetable")) {
                                        restartApplication()
                                        Toast.makeText(activity, resources.getString(R.string.restore_successful), Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(activity, resources.getString(R.string.backup_not_found), Toast.LENGTH_SHORT).show()
                                    }
                                }
                                .setNegativeButton(resources.getString(R.string.no), null)
                                .create()
                        overwriteDialog.show()
                    }
                }
                alertDialog.setNegativeButton(resources.getString(R.string.no), null)
                alertDialog.show()
            } else {
                Toast.makeText(activity, resources.getString(R.string.no_permission_granted), Toast.LENGTH_SHORT).show()
            }
            false
        }
        reset.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val alertDialog = AlertDialog.Builder(activity)
            alertDialog.setTitle(resources.getString(R.string.reset_the_timetable))
            alertDialog.setMessage(resources.getString(R.string.reset_question))
            alertDialog.setPositiveButton(resources.getString(R.string.yes)) { dialog, which ->
                val resetDBPath = "data/data/com.zeeshanhussain.timetable/databases/"
                val TimetableDB = File(resetDBPath + "Timetable")
                if (TimetableDB.exists()) {
                    TimetableDB.delete()
                }
                restartApplication()
                Toast.makeText(mActivity, resources.getString(R.string.reset_successful), Toast.LENGTH_SHORT).show()
            }
            alertDialog.setNegativeButton(resources.getString(R.string.no), null)
            alertDialog.show()
            false
        }
    }

    fun addAlarm(alarm: Alarms) {
        alarmsList.add(alarm)
    }

    private fun restartApplication() {
        val mStartActivity = Intent(activity, MainActivity::class.java)
        val mPendingIntentId = 123456
        val mPendingIntent = PendingIntent.getActivity(activity, mPendingIntentId, mStartActivity,
                PendingIntent.FLAG_CANCEL_CURRENT)
        val mgr = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent)
        activity.finish()
        System.exit(0)
    }

    companion object {

        private val TAG = "MyPreferenceFragment"
        private val alarmsList = ArrayList<Alarms>()
    }
}
