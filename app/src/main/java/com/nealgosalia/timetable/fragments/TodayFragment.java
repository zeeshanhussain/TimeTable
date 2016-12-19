package com.nealgosalia.timetable.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nealgosalia.timetable.R;
import com.nealgosalia.timetable.adapters.LecturesAdapter;
import com.nealgosalia.timetable.database.FragmentDatabase;
import com.nealgosalia.timetable.utils.DividerItemDecoration;
import com.nealgosalia.timetable.utils.Lecture;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TodayFragment extends Fragment {

    private List<Lecture> lecturesList = new ArrayList<>();
    private RecyclerView recyclerLectures;
    private LecturesAdapter mLectureAdapter;
    private TextView placeholderText;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_today, container, false);
        FragmentDatabase db = new FragmentDatabase(getActivity());
        placeholderText = (TextView) view.findViewById(R.id.todayPlaceholderText);
        Calendar c = Calendar.getInstance();
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        if (Calendar.MONDAY == dayOfWeek) {
            lecturesList = new ArrayList<>(db.getLectureList(0));
        } else if (Calendar.TUESDAY == dayOfWeek) {
            lecturesList = new ArrayList<>(db.getLectureList(1));
        } else if (Calendar.WEDNESDAY == dayOfWeek) {
            lecturesList = new ArrayList<>(db.getLectureList(2));
        } else if (Calendar.THURSDAY == dayOfWeek) {
            lecturesList = new ArrayList<>(db.getLectureList(3));
        } else if (Calendar.FRIDAY == dayOfWeek) {
            lecturesList = new ArrayList<>(db.getLectureList(4));
        } else if (Calendar.SATURDAY == dayOfWeek) {
            lecturesList = new ArrayList<>(db.getLectureList(5));
        } else if (Calendar.SUNDAY == dayOfWeek) {
            lecturesList = new ArrayList<>(db.getLectureList(6));
        }
        if (lecturesList.size() != 0) {
            placeholderText.setVisibility(View.GONE);
        }
        recyclerLectures = (RecyclerView) view.findViewById(R.id.listToday);
        mLectureAdapter = new LecturesAdapter(lecturesList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerLectures.setLayoutManager(mLayoutManager);
        recyclerLectures.setItemAnimator(new DefaultItemAnimator());
        recyclerLectures.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerLectures.setAdapter(mLectureAdapter);
        return view;
    }
}
