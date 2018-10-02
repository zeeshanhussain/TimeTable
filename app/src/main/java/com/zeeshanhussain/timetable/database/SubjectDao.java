package com.zeeshanhussain.timetable.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

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
