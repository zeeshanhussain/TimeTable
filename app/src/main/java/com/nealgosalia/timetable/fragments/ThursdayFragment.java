package com.nealgosalia.timetable.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nealgosalia.timetable.R;
import com.nealgosalia.timetable.adapters.LecturesAdapter;
import com.nealgosalia.timetable.utils.DividerItemDecoration;
import com.nealgosalia.timetable.utils.FragmentDatabase;
import com.nealgosalia.timetable.utils.Lecture;

import java.util.ArrayList;
import java.util.List;



public class ThursdayFragment extends Fragment {

    public static List<Lecture> lecturesList = new ArrayList<>();
    private RecyclerView recyclerLectures;
    public static LecturesAdapter mLectureAdapter;
    private View v;

    public ThursdayFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v=inflater.inflate(R.layout.fragment_thursday, container, false);
        FragmentDatabase fragmentDatabase=new FragmentDatabase();
        lecturesList=new ArrayList<>(fragmentDatabase.getLectureList(3,getActivity()));
        recyclerLectures=(RecyclerView)v.findViewById(R.id.listThursday);
        mLectureAdapter = new LecturesAdapter(lecturesList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerLectures.setLayoutManager(mLayoutManager);
        recyclerLectures.setItemAnimator(new DefaultItemAnimator());
        recyclerLectures.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerLectures.setAdapter(mLectureAdapter);
        return v;
    }

}
