package com.zeeshanhussain.timetable.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Lecture(var day: Int, var subjectName: String?, var startHour: Int, var endHour: Int, var startMinute: Int, var endMinute: Int, var roomNo: String?) {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
