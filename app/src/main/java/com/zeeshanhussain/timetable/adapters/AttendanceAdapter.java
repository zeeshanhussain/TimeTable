package com.zeeshanhussain.timetable.adapters;

import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.zeeshanhussain.timetable.R;
import com.zeeshanhussain.timetable.model.Subject;

import java.util.List;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.MyViewHolder> {

    private List<Subject> subjectList;
    private List<Integer> progressList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView subjectName;
        public NumberProgressBar attendance;


        public MyViewHolder(View view) {
            super(view);
            subjectName = view.findViewById(R.id.subjectNameAttendance);
            attendance = view.findViewById(R.id.attendance_progress_bar);
        }
    }

    public AttendanceAdapter(List<Subject> subjectList, List<Integer> progress) {
        this.subjectList = subjectList;
        this.progressList = progress;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_attendance, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Subject subject = subjectList.get(position);
        holder.subjectName.setText(subject.getSubjectName());
        holder.attendance.setProgress(progressList.get(position));
        int attendedLectures,totalLectures,x;
        attendedLectures=subjectList.get(position).getAttendedLectures();
        totalLectures=subjectList.get(position).getTotalLectures();
        if (attendedLectures==0 && totalLectures==0){
            x=0;
        }else{
            x = attendedLectures * 100 / totalLectures;
        }
        int redColorValue = Color.parseColor("#FF0000");
        int blueColorValue = Color.parseColor("#4385F4");
        if(x>=75 || x==0){
            holder.attendance.setReachedBarColor(blueColorValue);
            holder.attendance.setProgressTextColor(blueColorValue);
        } else{
            holder.attendance.setReachedBarColor(redColorValue);
            holder.attendance.setProgressTextColor(redColorValue);
        }


    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }
}
