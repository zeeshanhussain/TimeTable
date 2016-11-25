package com.nealgosalia.timetable;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.nealgosalia.timetable.activities.SubjectsActivity;
import com.nealgosalia.timetable.activities.TimetableActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnSubjects,btnTimetable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSubjects = (Button) findViewById(R.id.btnSubjects);
        btnSubjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SubjectsActivity.class);
                startActivity(intent);
            }
        });
        btnTimetable = (Button) findViewById(R.id.btnTimetable);
        btnTimetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,TimetableActivity.class);
                startActivity(intent);
            }
        });
    }
}
