package com.zeeshanhussain.timetable.adapters;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.zeeshanhussain.timetable.R;
import com.zeeshanhussain.timetable.model.Subject;
import com.zeeshanhussain.timetable.ui.fragments.AttendanceFragment;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.MyViewHolder> {

    private List<Subject> subjectList;
    private List<Integer> progressList;

    int targetAttendance;

    public AttendanceAdapter(List<Subject> subjectList, List<Integer> progress,
                             @Nullable SharedPreferences prefs) {
        this.subjectList = subjectList;
        this.progressList = progress;
        if (prefs != null)
            targetAttendance = Integer.parseInt(
                    prefs.getString(AttendanceFragment.ATTENDANCE_PREFS,
                            AttendanceFragment.DEF_TARGET_ATTENDANCE));
        else
            targetAttendance = Integer.parseInt(AttendanceFragment.DEF_TARGET_ATTENDANCE);
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
        int attendedLectures, totalLectures, x;
        attendedLectures = subjectList.get(position).getAttendedLectures();
        totalLectures = subjectList.get(position).getTotalLectures();
        if (attendedLectures == 0 && totalLectures == 0) {
            x = 0;
        } else {
            x = attendedLectures * 100 / totalLectures;
        }
        int redColorValue = Color.parseColor("#FF0000");
        int blueColorValue = Color.parseColor("#4385F4");
        if (x >= targetAttendance || x == 0) {
            holder.attendance.setReachedBarColor(blueColorValue);
            holder.attendance.setProgressTextColor(blueColorValue);
        } else {
            holder.attendance.setReachedBarColor(redColorValue);
            holder.attendance.setProgressTextColor(redColorValue);
        }


    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView subjectName;
        NumberProgressBar attendance;


        MyViewHolder(View view) {
            super(view);
            subjectName = view.findViewById(R.id.subjectNameAttendance);
            attendance = view.findViewById(R.id.attendance_progress_bar);
        }
    }
}
