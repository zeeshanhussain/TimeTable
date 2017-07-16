package com.nealgosalia.timetable.database;

/**
 * Created by men_in_black007 on 11/12/16.
 */

public class SubjectDetails {

    private String subject;
    private int attendedLectures;
    private int totalLectures;

    public SubjectDetails() {
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

    public SubjectDetails(String subject, int attendedLectures, int totalLectures) {
        this.subject = subject;
        this.attendedLectures = attendedLectures;
        this.totalLectures = totalLectures;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}