package com.zeeshanhussain.timetable.ui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zeeshanhussain.timetable.R;
import com.zeeshanhussain.timetable.adapters.LecturesAdapter;
import com.zeeshanhussain.timetable.database.AppDatabase;
import com.zeeshanhussain.timetable.model.Lecture;
import com.zeeshanhussain.timetable.utils.AppExecutors;
import com.zeeshanhussain.timetable.utils.DividerItemDecoration;
import com.zeeshanhussain.timetable.utils.RecyclerItemClickListener;
import com.zeeshanhussain.timetable.viewmodel.LectureViewModel;
import com.zeeshanhussain.timetable.viewmodel.LectureViewModelFactory;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class WednesdayFragment extends Fragment {

    private List<Lecture> lecturesList = new ArrayList<>();
    private LecturesAdapter mLectureAdapter;
    private TextView placeholderText;
    private AppDatabase appDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wednesday, container, false);
        placeholderText = view.findViewById(R.id.wednesdayPlaceholderText);
        appDatabase = AppDatabase.getsInstance(getActivity());
        RecyclerView recyclerLectures = view.findViewById(R.id.listWednesday);
        mLectureAdapter = new LecturesAdapter(lecturesList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerLectures.setLayoutManager(mLayoutManager);
        recyclerLectures.setItemAnimator(new DefaultItemAnimator());
        recyclerLectures.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerLectures.setAdapter(mLectureAdapter);
        LectureViewModelFactory lectureViewModelFactory = new LectureViewModelFactory(appDatabase, 2);
        LectureViewModel lectureViewModel = ViewModelProviders.of(this, lectureViewModelFactory).get(LectureViewModel.class);
        lectureViewModel.getLecture().observe(this, new Observer<List<Lecture>>() {
            @Override
            public void onChanged(@Nullable List<Lecture> lectures) {
                lecturesList.clear();
                lecturesList.addAll(lectures);
                mLectureAdapter.notifyDataSetChanged();
                if (lecturesList.size() != 0) {
                    placeholderText.setVisibility(View.GONE);
                }
            }
        });
        recyclerLectures.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Lecture lecture = lecturesList.get(position);
                showDeleteDialog(lecture, position);
            }
        }));
        return view;
    }

    private void showDeleteDialog(final Lecture lecture, final int position) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setTitle(getResources().getString(R.string.delete));
        dialogBuilder.setMessage(getResources().getString(R.string.delete_lecture));
        dialogBuilder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        appDatabase.lectureDao().delete(lecture);
                        lecturesList.remove(position);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mLectureAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });

            }
        });
        dialogBuilder.setNegativeButton(getResources().getString(R.string.no), null);
        AlertDialog b = dialogBuilder.create();
        b.show();
    }
}