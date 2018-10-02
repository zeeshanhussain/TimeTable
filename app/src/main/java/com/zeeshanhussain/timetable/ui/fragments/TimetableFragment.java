package com.zeeshanhussain.timetable.ui.fragments;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.zeeshanhussain.timetable.database.AppDatabase;
import com.zeeshanhussain.timetable.utils.AppExecutors;
import com.zeeshanhussain.timetable.viewmodel.MainViewModel;
import com.zeeshanhussain.timetable.R;
import com.zeeshanhussain.timetable.adapters.SimpleFragmentPagerAdapter;
import com.zeeshanhussain.timetable.receivers.MyReceiver;
import com.zeeshanhussain.timetable.utils.Alarms;
import com.zeeshanhussain.timetable.model.Lecture;
import com.zeeshanhussain.timetable.model.Subject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.content.Context.ALARM_SERVICE;


public class TimetableFragment extends Fragment {

    private static final int MINUTE = 60000;
    private static final String TAG = "TimetableFragment";
    private Button btnNext;
    private List<String> subjectsList = new ArrayList<>();
    private Spinner spinnerSubjects;
    private TabLayout tabLayout;
    private TextView textDialog;
    private TimePicker startTime;
    private TimePicker endTime;
    private ViewPager viewPager;
    private EditText roomN;
    private int count;
    private AppDatabase appDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_timetable, container, false);
        viewPager = view.findViewById(R.id.viewpager);
        viewPager.setAdapter(new SimpleFragmentPagerAdapter(getChildFragmentManager(), getActivity()));
        tabLayout = view.findViewById(R.id.sliding_tabs);
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
        appDatabase=AppDatabase.getsInstance(getActivity());
        setSubjectList();
        FloatingActionButton fab = view.findViewById(R.id.fabTimeTable);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count = 0;
                showTimeTableDialog();
            }
        });
        return view;
    }

    public void showTimeTableDialog() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams") final View dialogView = inflater.inflate(R.layout.dialog_add_timetable, null);

        startTime = dialogView.findViewById(R.id.startTime);
        endTime = dialogView.findViewById(R.id.endTime);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        btnNext = dialogView.findViewById(R.id.btnNext);
        textDialog = dialogView.findViewById(R.id.textDialog);
        spinnerSubjects = dialogView.findViewById(R.id.spinnerSubjects);
        roomN = dialogView.findViewById(R.id.room);
        Log.d("Tag",String.valueOf(subjectsList.size()));
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, subjectsList) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
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
        roomN.setVisibility(View.GONE);
        dialogBuilder.setView(dialogView);
        textDialog.setText(getResources().getString(R.string.choose_subject));
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
                        textDialog.setText(getResources().getString(R.string.enter_room_number));
                        roomN.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(getActivity(), "Please select a subject", Toast.LENGTH_SHORT).show();
                        count--;
                    }
                } else if(count==2) {
                    roomN.setVisibility(View.GONE);
                    textDialog.setText(getResources().getString(R.string.enter_start_time));
                    startTime.setVisibility(View.VISIBLE);
                    btnNext.setText(getResources().getString(R.string.next));
                }
                else if (count == 3) {
                    startTime.setVisibility(View.GONE);
                    textDialog.setText(getResources().getString(R.string.enter_end_time));
                    endTime.setVisibility(View.VISIBLE);
                    btnNext.setText(getResources().getString(R.string.done));
                } else if (count == 4) {
                    final int startHour, startMinute, endHour, endMinute;
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
                        final String edit  = roomN.getText().toString().trim();
                        final int day = tabLayout.getSelectedTabPosition();
                        final String subjectName = subjectsList.get(spinnerSubjects.getSelectedItemPosition());
                        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                appDatabase.lectureDao().insert(new Lecture(day, subjectName, startHour, endHour, startMinute, endMinute,edit));

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        viewPager.getAdapter().notifyDataSetChanged();
                                    }
                                });
                            }
                        });
                        dialog.dismiss();
                        SharedPreferences mSharedPreference = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
                        int notificationTime = Integer.parseInt(mSharedPreference.getString("NOTIFICATION_TIME", "-1"));
                        Log.d(TAG,Integer.toString(notificationTime));
                        if (notificationTime != -1) {
                            setAlarmForNotification(subjectName, day, notificationTime, startHour, startMinute);
                        }
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.end_time_should_be), Toast.LENGTH_LONG).show();
                        count--;
                    }
                }

            }
        });
        dialog.show();
    }

    private void setSubjectList() {
        MainViewModel mainViewModel= ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.getSubject().observe(this, new Observer<List<Subject>>() {
            @Override
            public void onChanged(@Nullable List<Subject> subjects) {
                int breakFlag = 0;
                subjectsList.clear();
                subjectsList.add("Select one");
                for(int i=0;i<subjects.size();i++){
                    if(breakFlag==0){
                        if(subjects.get(i).getSubjectName().compareTo("Break") >0){
                            subjectsList.add(getResources().getString(R.string.Break));
                            breakFlag++;
                        }
                    }
                    subjectsList.add(subjects.get(i).getSubjectName());
                }
                if (breakFlag == 0) {
                    subjectsList.add(getResources().getString(R.string.Break));
                }
            }
        });
    }

    private void setAlarmForNotification(String subjectName, int day, int notificationTime, int startHour, int startMinute) {

        int dayOfWeek = (day +1 % 7)+1;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        calendar.set(Calendar.HOUR_OF_DAY, startHour);
        calendar.set(Calendar.MINUTE, startMinute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.setTimeInMillis(calendar.getTimeInMillis() - notificationTime * MINUTE);
        if (System.currentTimeMillis() > calendar.getTimeInMillis()){
            calendar.set(Calendar.WEEK_OF_MONTH, calendar.get(Calendar.WEEK_OF_MONTH) + 1);
        }
        Log.d(TAG, String.valueOf(calendar.getTimeInMillis()));
        Intent myIntent = new Intent(getActivity(), MyReceiver.class);
        int requestCode = (int) System.currentTimeMillis()/1000;
        myIntent.putExtra("SUBJECT_NAME", subjectName);
        myIntent.putExtra("START_TIME", String.format(Locale.getDefault(), "%02d:%02d", startHour, startMinute));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), requestCode, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Alarms alarms = new Alarms();
        alarms.setContext(getActivity());
        alarms.setPendingIntent(pendingIntent);
        MyPreferenceFragment mpf = new MyPreferenceFragment();
        mpf.addAlarm(alarms);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);
    }
}
