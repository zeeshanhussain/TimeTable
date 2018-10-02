package com.zeeshanhussain.timetable.model

import java.util.Comparator

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
class Subject : Comparable<Subject> {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var subjectName: String? = null
    var attendedLectures: Int = 0
    var totalLectures: Int = 0

    @Ignore
    constructor() {
    }

    @Ignore
    constructor(subjectName: String) {
        this.subjectName = subjectName
    }

    constructor(subjectName: String, attendedLectures: Int, totalLectures: Int) {
        this.subjectName = subjectName
        this.attendedLectures = attendedLectures
        this.totalLectures = totalLectures
    }

    override fun compareTo(o: Subject): Int {
        return Comparators.NAME.compare(this, o)
    }

    object Comparators {

        var NAME: Comparator<Subject> = Comparator { o1, o2 -> o1.subjectName!!.compareTo(o2.subjectName!!) }
    }
}
