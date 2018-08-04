package com.zeeshanhussain.timetable.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.zeeshanhussain.timetable.database.AppDatabase;

public class LectureViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private AppDatabase appDatabase;
    private int day;

    public LectureViewModelFactory(AppDatabase database,int mDay){
        appDatabase=database;
        day=mDay;
    }
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new LectureViewModel(appDatabase,day);
    }
}
