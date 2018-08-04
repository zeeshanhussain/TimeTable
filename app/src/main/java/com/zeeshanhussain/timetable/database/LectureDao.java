package com.zeeshanhussain.timetable.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.zeeshanhussain.timetable.model.Lecture;

import java.util.List;

@Dao
public interface LectureDao {

    @Query("SELECT * FROM Lecture WHERE day = :day ORDER BY startHour,startMinute")
    LiveData<List<Lecture>> loadAllLectures(int day);

    @Query("SELECT * FROM Lecture")
    List<Lecture> loadAllLectures();

    @Insert
    void insert(Lecture lecture);

    @Delete
    void delete(Lecture lecture);
}
