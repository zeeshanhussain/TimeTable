package com.zeeshanhussain.timetable.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Comparator;

@Entity()
public class Subject implements Comparable<Subject> {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String subjectName;
    private int attendedLectures;
    private int totalLectures;

    @Ignore
    public Subject() {
    }

    @Ignore
    public Subject(String subjectName){
        this.subjectName=subjectName;
    }
    public  Subject(String subjectName,int attendedLectures,int totalLectures){
        this.subjectName=subjectName;
        this.attendedLectures=attendedLectures;
        this.totalLectures=totalLectures;
    }
    public int getAttendedLectures() {
        return attendedLectures;
    }

    public void setAttendedLectures(int attendedLectures) {
        this.attendedLectures = attendedLectures;
    }

    public int getTotalLectures() {
        return totalLectures;
    }

    public void setTotalLectures(int totalLectures) {
        this.totalLectures = totalLectures;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    @Override
    public int compareTo(Subject o) {
        return Comparators.NAME.compare(this, o);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static class Comparators {

        public static Comparator<Subject> NAME = new Comparator<Subject>() {
            @Override
            public int compare(Subject o1, Subject o2) {
                return o1.subjectName.compareTo(o2.subjectName);
            }
        };
    }
}
