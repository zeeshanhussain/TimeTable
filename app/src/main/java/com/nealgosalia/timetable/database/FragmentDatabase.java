package com.nealgosalia.timetable.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.nealgosalia.timetable.utils.Lecture;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FragmentDatabase extends SQLiteOpenHelper {

    private static final String TAG = "FragmentDatabase";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "lecture.db";
    private static final String TABLE_DETAIL = "lectureDetails";
    private static final String DAY = "day";
    private static final String SUBJECT = "subject";
    private static final String START_HOUR = "startHour";
    private static final String END_HOUR = "endHour";
    private static final String START_MINUTE = "startMinute";
    private static final String END_MINUTE = "endMinute";
    private static final String ROOM_NUMBER = "roomNo";
    private static final String CREATE_TABLE = "create table "
            + TABLE_DETAIL + "(" + DAY + " integer not null, " + SUBJECT + " varchar not null,"
            + START_HOUR + " integer not null," + START_MINUTE + " integer not null,"
            + END_HOUR + " integer not null," + END_MINUTE + " integer not null," +  ROOM_NUMBER + " varchar not null);";
    private List<Lecture> lecturesList = new ArrayList<>();

    public FragmentDatabase(Context context) {
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

    public void add(FragmentDetails fd) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DAY, fd.getDay());
        values.put(SUBJECT, fd.getSubject());
        values.put(START_HOUR, fd.getStartHour());
        values.put(START_MINUTE, fd.getStartMinute());
        values.put(END_HOUR, fd.getEndHour());
        values.put(END_MINUTE, fd.getEndMinute());
        values.put(ROOM_NUMBER,fd.getRoomNo());
        db.insert(TABLE_DETAIL, null, values);
        db.close();
    }

    public void remove(FragmentDetails fd) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = FragmentDatabase.DAY + "=? AND " + FragmentDatabase.START_HOUR + "=? AND "+ FragmentDatabase.START_MINUTE + "=?";
        String[] selectionArgs = { Integer.toString(fd.getDay()),Integer.toString(fd.getStartHour()),Integer.toString(fd.getEndMinute()) };
        db.delete(FragmentDatabase.TABLE_DETAIL, selection, selectionArgs);
    }

    public List getLectureList() {
        String sql = "select * from " + TABLE_DETAIL;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        try {
            lecturesList.clear();
            while (cursor.moveToNext()) {
                Lecture lecture = new Lecture();
                lecture.setSubjectName(cursor.getString(1));
                lecture.setStartTime(String.format(Locale.US, "%02d:%02d", cursor.getInt(2), cursor.getInt(3)));
                lecture.setEndTime(String.format(Locale.US, "%02d:%02d", cursor.getInt(4), cursor.getInt(5)));
                lecture.setDay(cursor.getInt(0));
                lecture.setRoomNo(cursor.getString(6));
                lecturesList.add(lecture);
            }
        } catch (Exception e) {
        } finally {
            cursor.close();
        }
        db.close();
        return lecturesList;
    }

    public List getLectureList(int pos) {
        String sql = "select * from " + TABLE_DETAIL + " where day=" + pos + " order by " + START_HOUR + "," + START_MINUTE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        try {
            lecturesList.clear();
            while (cursor.moveToNext()) {
                Lecture lecture = new Lecture();
                lecture.setSubjectName(cursor.getString(1));
                lecture.setStartTime(String.format(Locale.US, "%02d:%02d", cursor.getInt(2), cursor.getInt(3)));
                lecture.setEndTime(String.format(Locale.US, "%02d:%02d", cursor.getInt(4), cursor.getInt(5)));
                lecture.setDay(cursor.getInt(0));
                lecture.setRoomNo(cursor.getString(6));
                lecturesList.add(lecture);
            }
        } catch (Exception e) {
        } finally {
            cursor.close();
        }
        db.close();
        return lecturesList;
    }
}