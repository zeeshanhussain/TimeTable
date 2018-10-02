package com.zeeshanhussain.timetable.viewmodel

import android.app.Application

import com.zeeshanhussain.timetable.database.AppDatabase
import com.zeeshanhussain.timetable.model.Subject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val appDatabase: AppDatabase
    private var subject: LiveData<List<Subject>>? = null

    init {
        appDatabase = AppDatabase.getsInstance(application)
    }

    fun getSubject(): LiveData<List<Subject>> {
        if (subject == null) {
            subject = MutableLiveData()
            loadSubjects()
        }
        return subject as LiveData<List<Subject>>
    }


    private fun loadSubjects() {
        subject = appDatabase.subjectDao().loadAllSubjects()
    }

    fun insertSubject(subject: Subject) {
        appDatabase.subjectDao().insert(subject)
    }

    fun updateSubject(name: String, id: Int) {
        appDatabase.subjectDao().updateSubject(name, id)
    }

    fun deleteSubject(subject: Subject) {
        appDatabase.subjectDao().delete(subject)
    }

    fun updateAttendance(attend: Int, total: Int, id: Int) {
        appDatabase.subjectDao().updateAttendance(attend, total, id)
    }


}
