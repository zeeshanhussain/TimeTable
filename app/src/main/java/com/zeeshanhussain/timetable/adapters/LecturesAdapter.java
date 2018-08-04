package com.zeeshanhussain.timetable.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zeeshanhussain.timetable.R;
import com.zeeshanhussain.timetable.model.Lecture;

import java.util.List;
import java.util.Locale;

public class LecturesAdapter extends RecyclerView.Adapter<LecturesAdapter.MyViewHolder> {

    private List<Lecture> lectureList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView lectureName;
        public TextView lectureTime;
        public TextView lectureRoom;

        public MyViewHolder(View view) {
            super(view);
            lectureName = view.findViewById(R.id.lectureName);
            lectureTime = view.findViewById(R.id.lectureTime);
            lectureRoom = view.findViewById(R.id.lectureRoom);

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
        String startTime= String.format(Locale.getDefault(), "%02d:%02d", lecture.getStartHour(), lecture.getStartMinute());
        String endTime=String.format(Locale.getDefault(), "%02d:%02d", lecture.getEndHour(), lecture.getEndMinute());
        holder.lectureTime.setText(startTime + " - " + endTime);
        if(!lecture.getRoomNo().isEmpty()){
                        holder.lectureRoom.setText("Room Number - "+lecture.getRoomNo());
                    }

    }

    @Override
    public int getItemCount() {
        return lectureList.size();
    }
}