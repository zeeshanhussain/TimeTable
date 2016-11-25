package com.nealgosalia.timetable.utils;

import java.util.Comparator;

public class Subject implements Comparable<Subject>{

    private String subjectName;

    public Subject(){
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


    public static class Comparators {

        public static Comparator<Subject> NAME = new Comparator<Subject>() {
            @Override
            public int compare(Subject o1, Subject o2) {
                return o1.subjectName.compareTo(o2.subjectName);
            }
        };
    }
}
