package com.nealgosalia.timetable.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.nealgosalia.timetable.R;
import com.nealgosalia.timetable.adapters.SimpleFragmentPagerAdapter;
import com.nealgosalia.timetable.database.FragmentDatabase;
import com.nealgosalia.timetable.database.FragmentDetails;
import com.nealgosalia.timetable.database.SubjectDatabase;
import com.nealgosalia.timetable.database.SubjectDetails;
import com.nealgosalia.timetable.fragments.MyPreferenceFragment;
import com.nealgosalia.timetable.receivers.MyReceiver;
import com.nealgosalia.timetable.utils.Alarms;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class TimetableActivity extends AppCompatActivity {

    private static final int MINUTE = 60000;
    private static final String TAG = "TimetableActivity";
    private Button btnCancel, btnNext;
    private List<String> subjectsList = new ArrayList<>();
    private Spinner spinnerSubjects;
    private TabLayout tabLayout;
    private TextView textDialog;
    private TimePicker startTime;
    private TimePicker endTime;
    private ViewPager viewPager;
    private int count;
    private int breakFlag;
    private FragmentDatabase fragmentDatabase;
    private SubjectDatabase subjectDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
        }
        fragmentDatabase = new FragmentDatabase(this);
        subjectDatabase = new SubjectDatabase(this);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new SimpleFragmentPagerAdapter(getSupportFragmentManager(), TimetableActivity.this));
        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        Calendar c = Calendar.getInstance();
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        if (Calendar.MONDAY == dayOfWeek) {
            viewPager.setCurrentItem(0);
        } else if (Calendar.TUESDAY == dayOfWeek) {
            viewPager.setCurrentItem(1);
        } else if (Calendar.WEDNESDAY == dayOfWeek) {
            viewPager.setCurrentItem(2);
        } else if (Calendar.THURSDAY == dayOfWeek) {
            viewPager.setCurrentItem(3);
        } else if (Calendar.FRIDAY == dayOfWeek) {
            viewPager.setCurrentItem(4);
        } else if (Calendar.SATURDAY == dayOfWeek) {
            viewPager.setCurrentItem(5);
        } else if (Calendar.SUNDAY == dayOfWeek) {
            viewPager.setCurrentItem(6);
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabTimeTable);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count = 0;
                showTimeTableDialog();
            }
        });
    }

    public void showTimeTableDialog() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_add_timetable, null);

        startTime = (TimePicker) dialogView.findViewById(R.id.startTime);
        endTime = (TimePicker) dialogView.findViewById(R.id.endTime);
        btnCancel = (Button) dialogView.findViewById(R.id.btnCancel);
        btnNext = (Button) dialogView.findViewById(R.id.btnNext);
        textDialog = (TextView) dialogView.findViewById(R.id.textDialog);
        spinnerSubjects = (Spinner) dialogView.findViewById(R.id.spinnerSubjects);
        setSubjectList();
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, subjectsList) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSubjects.setAdapter(spinnerAdapter);

        startTime.setVisibility(View.GONE);
        endTime.setVisibility(View.GONE);
        dialogBuilder.setView(dialogView);
        textDialog.setText("Choose subject");
        final AlertDialog dialog = dialogBuilder.create();
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count++;
                if (count == 1) {
                    if (spinnerSubjects.getSelectedItemPosition() != 0) {
                        spinnerSubjects.setVisibility(View.GONE);
                        textDialog.setText("Enter start time");
                        startTime.setVisibility(View.VISIBLE);
                        btnNext.setText("Next");
                    } else {
                        Toast.makeText(TimetableActivity.this, "Please select a subject", Toast.LENGTH_SHORT).show();
                        count--;
                    }
                } else if (count == 2) {
                    startTime.setVisibility(View.GONE);
                    textDialog.setText("Enter end time");
                    endTime.setVisibility(View.VISIBLE);
                    btnNext.setText("Done");
                } else if (count == 3) {
                    int startHour, startMinute, endHour, endMinute;
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        startHour = startTime.getCurrentHour();
                        startMinute = startTime.getCurrentMinute();
                        endHour = endTime.getCurrentHour();
                        endMinute = endTime.getCurrentMinute();

                    } else {
                        startHour = startTime.getHour();
                        startMinute = startTime.getMinute();
                        endHour = endTime.getHour();
                        endMinute = endTime.getMinute();
                    }
                    if ((endHour > startHour) || ((endHour == startHour) && (endMinute > startMinute))) {
                        int day = tabLayout.getSelectedTabPosition();
                        String subjectName = subjectsList.get(spinnerSubjects.getSelectedItemPosition());
                        fragmentDatabase.add(new FragmentDetails(day, subjectName, startHour, startMinute, endHour, endMinute));
                        dialog.dismiss();
                        viewPager.getAdapter().notifyDataSetChanged();
                        SharedPreferences mSharedPreference = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                        int notificationTime = Integer.parseInt(mSharedPreference.getString("NOTIFICATION_TIME", "-1"));
                        Log.d(TAG,Integer.toString(notificationTime));
                        if (notificationTime != -1) {
                            setAlarmForNotification(subjectName, day, notificationTime, startHour, startMinute);
                        }
                    } else {
                        Toast.makeText(TimetableActivity.this, "End time should be greater than start time!", Toast.LENGTH_LONG).show();
                        count--;
                    }
                }
            }
        });
        dialog.show();
    }

    private void setSubjectList() {
        breakFlag = 0;
        subjectsList.clear();
        subjectsList.add("Select one");
        for (SubjectDetails subjectDetails : subjectDatabase.getSubjectDetail()) {
            if (breakFlag == 0) {
                if (subjectDetails.getSubject().compareTo("Break") > 0) {
                    subjectsList.add("Break");
                    breakFlag++;
                }
            }
            subjectsList.add(subjectDetails.getSubject());
        }
        if (breakFlag == 0) {
            subjectsList.add("Break");
        }
    }

    private void setAlarmForNotification(String subjectName, int day, int notificationTime, int startHour, int startMinute) {
        int dayOfWeek = (day + 2) % 7;
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.DAY_OF_WEEK) > dayOfWeek) {
            calendar.set(Calendar.WEEK_OF_MONTH, calendar.get(Calendar.WEEK_OF_MONTH) + 1);
        }
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        calendar.set(Calendar.HOUR_OF_DAY, startHour);
        calendar.set(Calendar.MINUTE, startMinute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.setTimeInMillis(calendar.getTimeInMillis() - notificationTime * MINUTE);
        Intent myIntent = new Intent(TimetableActivity.this, MyReceiver.class);
        int requestCode = (int) System.currentTimeMillis()/1000;
        myIntent.putExtra("SUBJECT_NAME", subjectName);
        myIntent.putExtra("START_TIME", String.format(Locale.US, "%02d:%02d", startHour, startMinute));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(TimetableActivity.this, requestCode, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Alarms alarms = new Alarms();
        alarms.setContext(TimetableActivity.this);
        alarms.setPendingIntent(pendingIntent);
        MyPreferenceFragment mpf = new MyPreferenceFragment();
        mpf.addAlarm(alarms);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);
    }
}
