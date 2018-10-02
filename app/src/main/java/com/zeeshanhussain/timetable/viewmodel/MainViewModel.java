package com.zeeshanhussain.timetable.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;

import com.zeeshanhussain.timetable.database.AppDatabase;
import com.zeeshanhussain.timetable.model.Subject;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private AppDatabase appDatabase;
    private LiveData<List<Subject>> subject;
    public MainViewModel(@NonNull Application application) {
        super(application);
        appDatabase = AppDatabase.getsInstance(application);
    }

    public LiveData<List<Subject>> getSubject() {
        if(subject==null){
            subject=new MutableLiveData<>();
            loadSubjects();
        }
        return subject;
    }


    private void loadSubjects() {
        subject=appDatabase.subjectDao().loadAllSubjects();
    }

    public void insertSubject(Subject subject){
        appDatabase.subjectDao().insert(subject);
    }

    public void updateSubject(String name,int id){
        appDatabase.subjectDao().updateSubject(name,id);
    }

    public void deleteSubject(Subject subject){
        appDatabase.subjectDao().delete(subject);
    }

    public void updateAttendance(int attend,int total,int id){
        appDatabase.subjectDao().updateAttendance(attend,total,id);
    }


}
