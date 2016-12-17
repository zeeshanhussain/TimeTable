package com.nealgosalia.timetable.utils;

import java.util.Comparator;

public class Lecture implements Comparable<Lecture> {

    private String subjectName;
    private String startTime;
    private String endTime;
    private int day;

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Override
    public int compareTo(Lecture o) {
        return Lecture.Comparators.NAME.compare(this, o);
    }

    public static class Comparators {

        public static Comparator<Lecture> NAME = new Comparator<Lecture>() {
            @Override
            public int compare(Lecture o1, Lecture o2) {
                return o1.startTime.compareTo(o2.startTime);
            }
        };
    }
}
