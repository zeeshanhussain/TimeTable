package com.nealgosalia.timetable.database;

/**
 * Created by men_in_black007 on 10/12/16.
 */

public class FragmentDetails {

    private int day;
    private String subject;
    private int startHour;
    private int endHour;
    private int startMinute;
    private int endMinute;

    public FragmentDetails(int day, String subject, int startHour, int startMinute, int endHour, int endMinute) {
        this.day = day;
        this.subject = subject;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
    }

    public int getDay() {
        return day;
    }

    public String getSubject() {
        return subject;
    }

    public int getStartHour() {
        return startHour;
    }

    public int getEndHour() {
        return endHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public int getEndMinute() {
        return endMinute;
    }
}