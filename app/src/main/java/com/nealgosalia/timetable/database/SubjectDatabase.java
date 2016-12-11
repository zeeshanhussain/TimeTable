package com.nealgosalia.timetable.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by men_in_black007 on 11/12/16.
 */

public class SubjectDatabase extends SQLiteOpenHelper {

    private static final String TAG = "SubjectDatabase";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "subject.db";
    private static final String TABLE_DETAIL = "subjectName";
    private static final String SUBJECT = "subject";
    private static final String CREATE_TABLE = "create table " + TABLE_DETAIL + "(" + SUBJECT + " varchar);";

    public SubjectDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DETAIL);
        onCreate(db);
    }

    public void addSubject(SubjectDetails sd) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SUBJECT, sd.getSubject());
        db.insert(TABLE_DETAIL, null, values);
        db.close();
    }

    public ArrayList<SubjectDetails> getSubjectDetail() {
        ArrayList<SubjectDetails> allSubjects = new ArrayList<SubjectDetails>();
        String sql = "select distinct * from " + TABLE_DETAIL + " order by " + SUBJECT;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(sql, null);
        try {
            while (c.moveToNext()) {
                SubjectDetails subjectDetails = new SubjectDetails();
                subjectDetails.setSubject(c.getString(c.getColumnIndex(SUBJECT)));
                allSubjects.add(subjectDetails);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            c.close();
        }
        db.close();
        return allSubjects;
    }
}