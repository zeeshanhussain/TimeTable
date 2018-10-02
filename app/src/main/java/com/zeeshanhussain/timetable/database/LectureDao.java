package com.zeeshanhussain.timetable.database;

import com.zeeshanhussain.timetable.model.Lecture;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

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
