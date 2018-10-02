package com.zeeshanhussain.timetable.viewmodel

import com.zeeshanhussain.timetable.database.AppDatabase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class LectureViewModelFactory(private val appDatabase: AppDatabase, private val day: Int) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        return LectureViewModel(appDatabase, day) as T
    }
}
