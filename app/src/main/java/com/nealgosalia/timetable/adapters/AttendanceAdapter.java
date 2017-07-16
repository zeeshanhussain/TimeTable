package com.nealgosalia.timetable.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.nealgosalia.timetable.R;
import com.nealgosalia.timetable.utils.Subject;

import java.util.List;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.MyViewHolder> {

    private List<Subject> subjectList;
    private List<Integer> progressList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView subjectName;
        public NumberProgressBar attendance;

        public MyViewHolder(View view) {
            super(view);
            subjectName = (TextView) view.findViewById(R.id.subjectNameAttendance);
            attendance = (NumberProgressBar)view.findViewById(R.id.attendance_progress_bar);
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
    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }
}
