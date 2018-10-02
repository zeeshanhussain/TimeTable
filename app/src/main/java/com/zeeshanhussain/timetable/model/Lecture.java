package com.zeeshanhussain.timetable.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity()
public class Lecture {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String subjectName;
    private int startHour;
    private int endHour;
    private int startMinute;
    private int endMinute;
    private String roomNo;
    private int day;

    public Lecture(int day, String subjectName, int startHour, int endHour, int startMinute, int endMinute, String roomNo) {

        this.day = day;
        this.subjectName = subjectName;
        this.startHour = startHour;
        this.endHour = endHour;
        this.startMinute = startMinute;
        this.endMinute = endMinute;
        this.roomNo = roomNo;

    }


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

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public void setEndMinute(int endMinute) {
        this.endMinute = endMinute;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
