package com.zeeshanhussain.timetable.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.zeeshanhussain.timetable.model.Subject;

import java.util.List;

@Dao
public interface SubjectDao {
    @Query("SELECT * FROM Subject ORDER BY subjectName")
    LiveData<List<Subject>> loadAllSubjects();

    @Insert
    void insert(Subject subject);

    @Query("UPDATE Subject SET subjectName = :name WHERE id= :id ")
    void updateSubject(String name,int id);

    @Query("UPDATE Subject SET attendedLectures= :attend ,totalLectures= :total WHERE id=:id")
    void updateAttendance(int attend,int total,int id );

    @Delete
    void delete(Subject subject);
}
