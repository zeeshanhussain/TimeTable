package com.zeeshanhussain.timetable.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.zeeshanhussain.timetable.R
import com.zeeshanhussain.timetable.model.Lecture
import java.util.Locale

import androidx.recyclerview.widget.RecyclerView

class LecturesAdapter(private val lectureList: List<Lecture>) : RecyclerView.Adapter<LecturesAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_lectures, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val lecture = lectureList[position]
        holder.lectureName.text = lecture.subjectName
        val startTime = String.format(Locale.getDefault(), "%02d:%02d", lecture.startHour, lecture.startMinute)
        val endTime = String.format(Locale.getDefault(), "%02d:%02d", lecture.endHour, lecture.endMinute)
        holder.lectureTime.text = "$startTime - $endTime"
        if (!lecture.roomNo!!.isEmpty()) {
            holder.lectureRoom.text = "Room Number - " + lecture.roomNo
        }

    }

    override fun getItemCount(): Int {
        return lectureList.size
    }

    inner class MyViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        internal var lectureName: TextView
        internal var lectureTime: TextView
        internal var lectureRoom: TextView

        init {
            lectureName = view.findViewById(R.id.lectureName)
            lectureTime = view.findViewById(R.id.lectureTime)
            lectureRoom = view.findViewById(R.id.lectureRoom)

        }
    }
}