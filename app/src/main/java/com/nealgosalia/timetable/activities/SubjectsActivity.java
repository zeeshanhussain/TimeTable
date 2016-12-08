package com.nealgosalia.timetable.activities;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.nealgosalia.timetable.utils.DividerItemDecoration;
import com.nealgosalia.timetable.R;
import com.nealgosalia.timetable.utils.Subject;
import com.nealgosalia.timetable.adapters.SubjectsAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class SubjectsActivity extends AppCompatActivity {

    private List<Subject> subjectsList = new ArrayList<>();
    private RecyclerView listSubjects;
    private SubjectsAdapter mSubjectsAdapter;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjects);
        listSubjects=(RecyclerView)findViewById(R.id.listSubjects);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        database = openOrCreateDatabase("Subjects",MODE_PRIVATE,null);
        database.execSQL("CREATE TABLE IF NOT EXISTS Subjects(Subject VARCHAR);");

        Cursor cursor = database.rawQuery("SELECT * FROM Subjects ORDER BY Subject",null);
        try {
            subjectsList.clear();
            while (cursor.moveToNext()) {
                String tempSubject=cursor.getString(0);
                Subject subject=new Subject();
                subject.setSubjectName(tempSubject);
                subjectsList.add(subject);
            }
        } catch (Exception e){
            Toast.makeText(SubjectsActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        finally{
            cursor.close();
        }

        mSubjectsAdapter = new SubjectsAdapter(subjectsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        listSubjects.setLayoutManager(mLayoutManager);
        listSubjects.setItemAnimator(new DefaultItemAnimator());
        listSubjects.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        listSubjects.setAdapter(mSubjectsAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSubjectDialog();
            }
        });
    }

    public void showSubjectDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_add_subject, null);
        dialogBuilder.setView(dialogView);

        final EditText newSubjectName = (EditText) dialogView.findViewById(R.id.newSubjectName);

        dialogBuilder.setTitle("Subject");
        dialogBuilder.setMessage("Enter subject name");
        dialogBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Subject subject=new Subject();
                String tempSubject=newSubjectName.getText().toString().trim();
                subject.setSubjectName(tempSubject);
                database.execSQL("INSERT INTO Subjects VALUES('"+tempSubject+"');");
                subjectsList.add(subject);
                Collections.sort(subjectsList,Subject.Comparators.NAME);
                mSubjectsAdapter.notifyDataSetChanged();
            }
        });
        dialogBuilder.setNegativeButton("Cancel", null);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
