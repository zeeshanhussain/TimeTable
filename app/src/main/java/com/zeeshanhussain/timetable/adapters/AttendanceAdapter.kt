package com.zeeshanhussain.timetable.adapters

import android.content.SharedPreferences
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.daimajia.numberprogressbar.NumberProgressBar
import com.zeeshanhussain.timetable.R
import com.zeeshanhussain.timetable.model.Subject
import com.zeeshanhussain.timetable.ui.fragments.AttendanceFragment
import androidx.recyclerview.widget.RecyclerView

class AttendanceAdapter(private val subjectList: List<Subject>, private val progressList: List<Int>,
                        prefs: SharedPreferences?) : RecyclerView.Adapter<AttendanceAdapter.MyViewHolder>() {

    internal var targetAttendance: Int = 0

    init {
        if (prefs != null)
            targetAttendance = Integer.parseInt(
                    prefs.getString(AttendanceFragment.ATTENDANCE_PREFS,
                            AttendanceFragment.DEF_TARGET_ATTENDANCE)!!)
        else
            targetAttendance = Integer.parseInt(AttendanceFragment.DEF_TARGET_ATTENDANCE)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_attendance, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val subject = subjectList[position]
        holder.subjectName.text = subject.subjectName
        holder.attendance.progress = progressList[position]
        val attendedLectures: Int
        val totalLectures: Int
        val x: Int
        attendedLectures = subjectList[position].attendedLectures
        totalLectures = subjectList[position].totalLectures
        if (attendedLectures == 0 && totalLectures == 0) {
            x = 0
        } else {
            x = attendedLectures * 100 / totalLectures
        }
        val redColorValue = Color.parseColor("#FF0000")
        val blueColorValue = Color.parseColor("#4385F4")
        if (x >= targetAttendance || x == 0) {
            holder.attendance.reachedBarColor = blueColorValue
            holder.attendance.setProgressTextColor(blueColorValue)
        } else {
            holder.attendance.reachedBarColor = redColorValue
            holder.attendance.setProgressTextColor(redColorValue)
        }


    }

    override fun getItemCount(): Int {
        return subjectList.size
    }

    inner class MyViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        internal var subjectName: TextView
        internal var attendance: NumberProgressBar


        init {
            subjectName = view.findViewById(R.id.subjectNameAttendance)
            attendance = view.findViewById(R.id.attendance_progress_bar)
        }
    }
}
