package com.zeeshanhussain.timetable.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.zeeshanhussain.timetable.R
import com.zeeshanhussain.timetable.model.Subject

import androidx.recyclerview.widget.RecyclerView

class SubjectsAdapter(private val subjectsList: List<Subject>) : RecyclerView.Adapter<SubjectsAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_subjects, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val subject = subjectsList[position]
        holder.subjectName.text = subject.subjectName
    }

    override fun getItemCount(): Int {
        return subjectsList.size
    }

    inner class MyViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        internal var subjectName: TextView

        init {
            subjectName = view.findViewById(R.id.subjectName)
        }
    }
}