package com.nealgosalia.timetable.utils;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class FragmentDatabase {

    private SQLiteDatabase database;
    private List<Lecture> lecturesList = new ArrayList<>();

    public List getLectureList(int pos, Activity activity){
        database = activity.openOrCreateDatabase("Entries",MODE_PRIVATE,null);
        database.execSQL("CREATE TABLE IF NOT EXISTS Entry(day INT, subject VARCHAR, startHour INT, startMinute INT, endHour INT, endMinute INT);");
        Cursor cursor = database.rawQuery("SELECT * FROM Entry WHERE day="+pos,null);
        try {
            lecturesList.clear();
            while (cursor.moveToNext()) {

                String subjectName=cursor.getString(1);
                int startH=cursor.getInt(2);
                int startM=cursor.getInt(3);
                int endH=cursor.getInt(4);
                int endM=cursor.getInt(5);
                String startHour,startMinute,endHour,endMinute;

                if(startH<10){
                    startHour="0"+startH;
                } else{
                    startHour=""+startH;
                }
                if(startM<10){
                    startMinute="0"+startM;
                } else{
                    startMinute=""+startM;
                }
                if(endH<10){
                    endHour="0"+endH;
                } else{
                    endHour=""+endH;
                }
                if(endM<10){
                    endMinute="0"+endM;
                } else{
                    endMinute=""+endM;
                }
                Lecture lecture=new Lecture();
                lecture.setSubjectName(subjectName);
                lecture.setStartTime(startHour+":"+startMinute);
                lecture.setEndTime(endHour+":"+endMinute);
                lecturesList.add(lecture);
            }
        } catch (Exception e){
        }
        finally{
            cursor.close();
        }
        return lecturesList;
    }
}
