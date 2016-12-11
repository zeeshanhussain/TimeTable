package com.nealgosalia.timetable.activities;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

import java.util.ArrayList;
import java.util.List;

public class TimetableActivity extends AppCompatActivity {

    private Button btnCancel, btnNext;
    private List<String> subjectsList = new ArrayList<>();
    private Spinner spinnerSubjects;
    private TabLayout tabLayout;
    private TextView textDialog;
    private TimePicker startTime;
    private TimePicker endTime;
    private int count;
    private int breakFlag;
    private FragmentDatabase fragmentDatabase;
    private SubjectDatabase subjectDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);
        getSupportActionBar().setElevation(0);
        fragmentDatabase = new FragmentDatabase(this);
        subjectDatabase = new SubjectDatabase(this);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new SimpleFragmentPagerAdapter(getSupportFragmentManager(), TimetableActivity.this));
        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
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
                        fragmentDatabase.add(new FragmentDetails(tabLayout.getSelectedTabPosition(), subjectsList.get(spinnerSubjects.getSelectedItemPosition()).toString(),
                                startHour, startMinute, endHour, endMinute));
                        dialog.dismiss();
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
                if(subjectDetails.getSubject().compareTo("Break")>0) {
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
}
