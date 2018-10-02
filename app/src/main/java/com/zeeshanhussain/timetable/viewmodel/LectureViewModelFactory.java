package com.zeeshanhussain.timetable.viewmodel;

import com.zeeshanhussain.timetable.database.AppDatabase;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class LectureViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private AppDatabase appDatabase;
    private int day;

    public LectureViewModelFactory(AppDatabase database, int mDay) {
        appDatabase = database;
        day = mDay;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new LectureViewModel(appDatabase, day);
    }
}
