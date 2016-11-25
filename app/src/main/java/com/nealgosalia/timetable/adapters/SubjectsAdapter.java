package com.nealgosalia.timetable.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nealgosalia.timetable.R;
import com.nealgosalia.timetable.utils.Subject;

import java.util.List;

public class SubjectsAdapter extends RecyclerView.Adapter<SubjectsAdapter.MyViewHolder> {

    private List<Subject> subjectsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView subjectName;

        public MyViewHolder(View view) {
            super(view);
            subjectName = (TextView) view.findViewById(R.id.subjectName);

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