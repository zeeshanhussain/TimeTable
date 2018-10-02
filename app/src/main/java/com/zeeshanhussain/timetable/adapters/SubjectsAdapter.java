package com.zeeshanhussain.timetable.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zeeshanhussain.timetable.R;
import com.zeeshanhussain.timetable.model.Subject;

import java.util.List;

public class SubjectsAdapter extends RecyclerView.Adapter<SubjectsAdapter.MyViewHolder> {

    private List<Subject> subjectsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView subjectName;

        MyViewHolder(View view) {
            super(view);
            subjectName = view.findViewById(R.id.subjectName);
        }
    }

    public SubjectsAdapter(List<Subject> subjectsList) {
        this.subjectsList = subjectsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_subjects, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Subject subject = subjectsList.get(position);
        holder.subjectName.setText(subject.getSubjectName());
    }

    @Override
    public int getItemCount() {
        return subjectsList.size();
    }
}