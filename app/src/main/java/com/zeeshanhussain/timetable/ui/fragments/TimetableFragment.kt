package com.zeeshanhussain.timetable.ui.fragments

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast

import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.zeeshanhussain.timetable.R
import com.zeeshanhussain.timetable.adapters.SimpleFragmentPagerAdapter
import com.zeeshanhussain.timetable.database.AppDatabase
import com.zeeshanhussain.timetable.model.Lecture
import com.zeeshanhussain.timetable.model.Subject
import com.zeeshanhussain.timetable.receivers.MyReceiver
import com.zeeshanhussain.timetable.utils.Alarms
import com.zeeshanhussain.timetable.utils.AppExecutors
import com.zeeshanhussain.timetable.viewmodel.MainViewModel

import java.util.ArrayList
import java.util.Calendar
import java.util.Locale
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager

import android.content.Context.ALARM_SERVICE


class TimetableFragment : Fragment() {
    private var btnNext: Button? = null
    private val subjectsList = ArrayList<String>()
    private var spinnerSubjects: Spinner? = null
    private var tabLayout: TabLayout? = null
    private var textDialog: TextView? = null
    private var startTime: TimePicker? = null
    private var endTime: TimePicker? = null
    private var viewPager: ViewPager? = null
    private var roomN: EditText? = null
    private var count: Int = 0
    private var appDatabase: AppDatabase? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_timetable, container, false)
        viewPager = view.findViewById(R.id.viewpager)
        viewPager!!.adapter = SimpleFragmentPagerAdapter(childFragmentManager, activity!!)
        tabLayout = view.findViewById(R.id.sliding_tabs)
        tabLayout!!.setupWithViewPager(viewPager)
        val c = Calendar.getInstance()
        val dayOfWeek = c.get(Calendar.DAY_OF_WEEK)
        if (Calendar.MONDAY == dayOfWeek) {
            viewPager!!.currentItem = 0
        } else if (Calendar.TUESDAY == dayOfWeek) {
            viewPager!!.currentItem = 1
        } else if (Calendar.WEDNESDAY == dayOfWeek) {
            viewPager!!.currentItem = 2
        } else if (Calendar.THURSDAY == dayOfWeek) {
            viewPager!!.currentItem = 3
        } else if (Calendar.FRIDAY == dayOfWeek) {
            viewPager!!.currentItem = 4
        } else if (Calendar.SATURDAY == dayOfWeek) {
            viewPager!!.currentItem = 5
        } else if (Calendar.SUNDAY == dayOfWeek) {
            viewPager!!.currentItem = 6
        }
        appDatabase = AppDatabase.getsInstance(activity)
        setSubjectList()
        val fab = view.findViewById<FloatingActionButton>(R.id.fabTimeTable)
        fab.setOnClickListener {
            count = 0
            showTimeTableDialog()
        }
        return view
    }

    private fun showTimeTableDialog() {
        val dialogBuilder = AlertDialog.Builder(activity!!)
        val inflater = activity!!.layoutInflater
        @SuppressLint("InflateParams") val dialogView = inflater.inflate(R.layout.dialog_add_timetable, null)

        startTime = dialogView.findViewById(R.id.startTime)
        endTime = dialogView.findViewById(R.id.endTime)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        btnNext = dialogView.findViewById(R.id.btnNext)
        textDialog = dialogView.findViewById(R.id.textDialog)
        spinnerSubjects = dialogView.findViewById(R.id.spinnerSubjects)
        roomN = dialogView.findViewById(R.id.room)
        Log.d("Tag", subjectsList.size.toString())
        val spinnerAdapter = object : ArrayAdapter<String>(activity!!, android.R.layout.simple_spinner_item, subjectsList) {
            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                val tv = view as TextView
                if (position == 0) {
                    tv.setTextColor(Color.GRAY)
                } else {
                    tv.setTextColor(Color.BLACK)
                }
                return view
            }
        }
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSubjects!!.adapter = spinnerAdapter

        startTime!!.visibility = View.GONE
        endTime!!.visibility = View.GONE
        roomN!!.visibility = View.GONE
        dialogBuilder.setView(dialogView)
        textDialog!!.text = resources.getString(R.string.choose_subject)
        val dialog = dialogBuilder.create()
        btnCancel.setOnClickListener { dialog.dismiss() }
        btnNext!!.setOnClickListener {
            count++
            if (count == 1) {
                if (spinnerSubjects!!.selectedItemPosition != 0) {
                    spinnerSubjects!!.visibility = View.GONE
                    textDialog!!.text = resources.getString(R.string.enter_room_number)
                    roomN!!.visibility = View.VISIBLE
                } else {
                    Toast.makeText(activity, "Please select a subject", Toast.LENGTH_SHORT).show()
                    count--
                }
            } else if (count == 2) {
                roomN!!.visibility = View.GONE
                textDialog!!.text = resources.getString(R.string.enter_start_time)
                startTime!!.visibility = View.VISIBLE
                btnNext!!.text = resources.getString(R.string.next)
            } else if (count == 3) {
                startTime!!.visibility = View.GONE
                textDialog!!.text = resources.getString(R.string.enter_end_time)
                endTime!!.visibility = View.VISIBLE
                btnNext!!.text = resources.getString(R.string.done)
            } else if (count == 4) {
                val startHour: Int
                val startMinute: Int
                val endHour: Int
                val endMinute: Int
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    startHour = startTime!!.currentHour
                    startMinute = startTime!!.currentMinute
                    endHour = endTime!!.currentHour
                    endMinute = endTime!!.currentMinute

                } else {
                    startHour = startTime!!.hour
                    startMinute = startTime!!.minute
                    endHour = endTime!!.hour
                    endMinute = endTime!!.minute
                }
                if (endHour > startHour || endHour == startHour && endMinute > startMinute) {
                    val edit = roomN!!.text.toString().trim { it <= ' ' }
                    val day = tabLayout!!.selectedTabPosition
                    val subjectName = subjectsList[spinnerSubjects!!.selectedItemPosition]
                    AppExecutors.instance.diskIO().execute {
                        appDatabase!!.lectureDao().insert(Lecture(day, subjectName, startHour, endHour, startMinute, endMinute, edit))

                        activity!!.runOnUiThread { viewPager!!.adapter!!.notifyDataSetChanged() }
                    }
                    dialog.dismiss()
                    val mSharedPreference = PreferenceManager.getDefaultSharedPreferences(activity!!.baseContext)
                    val notificationTime = Integer.parseInt(mSharedPreference.getString("NOTIFICATION_TIME", "-1")!!)
                    Log.d(TAG, Integer.toString(notificationTime))
                    if (notificationTime != -1) {
                        setAlarmForNotification(subjectName, day, notificationTime, startHour, startMinute)
                    }
                } else {
                    Toast.makeText(activity, resources.getString(R.string.end_time_should_be), Toast.LENGTH_LONG).show()
                    count--
                }
            }
        }
        dialog.show()
    }

    private fun setSubjectList() {
        val mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        mainViewModel.getSubject().observe(this, Observer { subjects ->
            var breakFlag = 0
            subjectsList.clear()
            subjectsList.add("Select one")
            for (i in subjects!!.indices) {
                if (breakFlag == 0) {
                    if (subjects[i].subjectName!!.compareTo("Break") > 0) {
                        subjectsList.add(resources.getString(R.string.Break))
                        breakFlag++
                    }
                }
                subjectsList.add(subjects[i].subjectName!!)
            }
            if (breakFlag == 0) {
                subjectsList.add(resources.getString(R.string.Break))
            }
        })
    }

    private fun setAlarmForNotification(subjectName: String, day: Int, notificationTime: Int, startHour: Int, startMinute: Int) {

        val dayOfWeek = day + 1 % 7 + 1
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek)
        calendar.set(Calendar.HOUR_OF_DAY, startHour)
        calendar.set(Calendar.MINUTE, startMinute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.timeInMillis = calendar.timeInMillis - notificationTime * MINUTE
        if (System.currentTimeMillis() > calendar.timeInMillis) {
            calendar.set(Calendar.WEEK_OF_MONTH, calendar.get(Calendar.WEEK_OF_MONTH) + 1)
        }
        Log.d(TAG, calendar.timeInMillis.toString())
        val myIntent = Intent(activity, MyReceiver::class.java)
        val requestCode = System.currentTimeMillis().toInt() / 1000
        myIntent.putExtra("SUBJECT_NAME", subjectName)
        myIntent.putExtra("START_TIME", String.format(Locale.getDefault(), "%02d:%02d", startHour, startMinute))
        val pendingIntent = PendingIntent.getBroadcast(activity, requestCode, myIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val alarms = Alarms()
        alarms.context = activity
        alarms.pendingIntent = pendingIntent
        val mpf = MyPreferenceFragment()
        mpf.addAlarm(alarms)
        val alarmManager = activity!!.getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY * 7, pendingIntent)
    }

    companion object {

        private val MINUTE = 60000
        private val TAG = "TimetableFragment"
    }
}
