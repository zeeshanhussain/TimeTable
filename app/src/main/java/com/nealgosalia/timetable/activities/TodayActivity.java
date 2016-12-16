package com.nealgosalia.timetable.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.nealgosalia.timetable.R;
import com.nealgosalia.timetable.adapters.LecturesAdapter;
import com.nealgosalia.timetable.database.FragmentDatabase;
import com.nealgosalia.timetable.receivers.MyReceiver;
import com.nealgosalia.timetable.utils.DividerItemDecoration;
import com.nealgosalia.timetable.utils.Lecture;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TodayActivity extends AppCompatActivity {

    private List<Lecture> lecturesList = new ArrayList<>();
    private RecyclerView recyclerLectures;
    private LecturesAdapter mLectureAdapter;
    private TextView placeholderText;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);
        FragmentDatabase db = new FragmentDatabase(this);
        placeholderText = (TextView) findViewById(R.id.todayPlaceholderText);
        Calendar c = Calendar.getInstance();
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        if (Calendar.MONDAY == dayOfWeek) {
            lecturesList = new ArrayList<>(db.getLectureList(0));
        } else if (Calendar.TUESDAY == dayOfWeek) {
            lecturesList = new ArrayList<>(db.getLectureList(1));
        } else if (Calendar.WEDNESDAY == dayOfWeek) {
            lecturesList = new ArrayList<>(db.getLectureList(2));
        } else if (Calendar.THURSDAY == dayOfWeek) {
            lecturesList = new ArrayList<>(db.getLectureList(3));
        } else if (Calendar.FRIDAY == dayOfWeek) {
            lecturesList = new ArrayList<>(db.getLectureList(4));
        } else if (Calendar.SATURDAY == dayOfWeek) {
            lecturesList = new ArrayList<>(db.getLectureList(5));
        } else if (Calendar.SUNDAY == dayOfWeek) {
            lecturesList = new ArrayList<>(db.getLectureList(6));
        }
        if(lecturesList.size()!=0){
            placeholderText.setVisibility(View.GONE);
        }
        recyclerLectures = (RecyclerView) findViewById(R.id.listToday);
        mLectureAdapter = new LecturesAdapter(lecturesList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerLectures.setLayoutManager(mLayoutManager);
        recyclerLectures.setItemAnimator(new DefaultItemAnimator());
        recyclerLectures.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerLectures.setAdapter(mLectureAdapter);

        // Notifications
        Calendar midnightCalendar = Calendar.getInstance();
        midnightCalendar.set(Calendar.HOUR_OF_DAY, 0);
        midnightCalendar.set(Calendar.MINUTE, 0);
        midnightCalendar.set(Calendar.SECOND, 0);
        midnightCalendar.set(Calendar.MILLISECOND, 0);
        long midnightMillis = midnightCalendar.getTimeInMillis();
        long currentMillis = c.getTimeInMillis();
        long diffMillis = currentMillis-midnightMillis;
        int currentHour = (int) diffMillis/(3600000);
        int currentMinute = (int) (diffMillis%3600000)/60000;
        int targetHour=0, targetMinute=0;
        if(lecturesList.size()!=0) {
            boolean lectureFound=false;
            for (Lecture lecture : lecturesList) {
                int startHour = Integer.parseInt(lecture.getStartTime().substring(0, 2));
                int startMinute = Integer.parseInt(lecture.getStartTime().substring(3, 5));
                if ((startHour > currentHour) || ((startHour == currentHour) && (startMinute > currentMinute))) {
                    if(startMinute>=5) {
                        targetHour = startHour;
                        targetMinute = startMinute - 5;
                    } else{
                        targetHour = startHour - 1;
                        targetMinute = 60 + startMinute - 5;
                    }
                    lectureFound=true;
                    break;
                }
            }
            if(lectureFound) {
                c.set(Calendar.HOUR_OF_DAY, targetHour);
                c.set(Calendar.MINUTE, targetMinute);
                Intent myIntent = new Intent(TodayActivity.this, MyReceiver.class);
                pendingIntent = PendingIntent.getBroadcast(TodayActivity.this, 0, myIntent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC, c.getTimeInMillis(), pendingIntent);
            }
        }
    }
}
