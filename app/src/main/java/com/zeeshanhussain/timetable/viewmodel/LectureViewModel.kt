package com.zeeshanhussain.timetable.viewmodel

import com.zeeshanhussain.timetable.database.AppDatabase
import com.zeeshanhussain.timetable.model.Lecture

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class LectureViewModel(database: AppDatabase, mDay: Int) : ViewModel() {
    val lecture: LiveData<List<Lecture>>

    init {
        lecture = database.lectureDao().loadAllLectures(mDay)
    }


}
