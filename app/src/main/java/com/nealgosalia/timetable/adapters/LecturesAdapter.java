package com.nealgosalia.timetable.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nealgosalia.timetable.R;
import com.nealgosalia.timetable.utils.Lecture;

import java.util.List;

public class LecturesAdapter extends RecyclerView.Adapter<LecturesAdapter.MyViewHolder> {

    private List<Lecture> lectureList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView lectureName;
        public TextView lectureTime;

        public MyViewHolder(View view) {
            super(view);
            lectureName = (TextView) view.findViewById(R.id.lectureName);
            lectureTime = (TextView) view.findViewById(R.id.lectureTime);
        }
    }

    public LecturesAdapter(List<Lecture> lectureList) {
        this.lectureList = lectureList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_lectures, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Lecture lecture = lectureList.get(position);
        holder.lectureName.setText(lecture.getSubjectName());
        holder.lectureTime.setText(lecture.getStartTime() + " - " + lecture.getEndTime());
    }

    @Override
    public int getItemCount() {
        return lectureList.size();
    }
}