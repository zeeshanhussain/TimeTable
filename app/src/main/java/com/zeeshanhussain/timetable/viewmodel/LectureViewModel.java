package com.zeeshanhussain.timetable.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.zeeshanhussain.timetable.database.AppDatabase;
import com.zeeshanhussain.timetable.model.Lecture;

import java.util.List;

public class LectureViewModel extends ViewModel {
    private LiveData<List<Lecture>> lecture;

    public LectureViewModel(AppDatabase database, int mDay){
        lecture=database.lectureDao().loadAllLectures(mDay);
    }
    public LiveData<List<Lecture>> getLecture(){
        return lecture;
    }


}
