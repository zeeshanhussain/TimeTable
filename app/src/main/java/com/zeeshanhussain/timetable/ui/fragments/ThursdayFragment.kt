package com.zeeshanhussain.timetable.ui.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.zeeshanhussain.timetable.R
import com.zeeshanhussain.timetable.adapters.LecturesAdapter
import com.zeeshanhussain.timetable.database.AppDatabase
import com.zeeshanhussain.timetable.model.Lecture
import com.zeeshanhussain.timetable.utils.AppExecutors
import com.zeeshanhussain.timetable.utils.DividerItemDecoration
import com.zeeshanhussain.timetable.utils.RecyclerItemClickListener
import com.zeeshanhussain.timetable.viewmodel.LectureViewModel
import com.zeeshanhussain.timetable.viewmodel.LectureViewModelFactory

import java.util.ArrayList
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ThursdayFragment : Fragment() {

    private val lecturesList = ArrayList<Lecture>()
    private var mLectureAdapter: LecturesAdapter? = null
    private var placeholderText: TextView? = null
    private var appDatabase: AppDatabase? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_thursday, container, false)
        placeholderText = view.findViewById(R.id.thursdayPlaceholderText)
        appDatabase = AppDatabase.getsInstance(activity)
        val recyclerLectures = view.findViewById<RecyclerView>(R.id.listThursday)
        mLectureAdapter = LecturesAdapter(lecturesList)
        val mLayoutManager = LinearLayoutManager(activity)
        recyclerLectures.layoutManager = mLayoutManager
        recyclerLectures.itemAnimator = DefaultItemAnimator()
        recyclerLectures.addItemDecoration(DividerItemDecoration(context!!, LinearLayoutManager.VERTICAL))
        recyclerLectures.adapter = mLectureAdapter
        val lectureViewModelFactory = LectureViewModelFactory(appDatabase!!, 3)
        val lectureViewModel = ViewModelProviders.of(this, lectureViewModelFactory).get(LectureViewModel::class.java)
        lectureViewModel.lecture.observe(this, Observer { lectures ->
            lecturesList.clear()
            lecturesList.addAll(lectures!!)
            mLectureAdapter!!.notifyDataSetChanged()
            if (lecturesList.size != 0) {
                placeholderText!!.visibility = View.GONE
            }
        })
        recyclerLectures.addOnItemTouchListener(RecyclerItemClickListener(context!!, object : RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val lecture = lecturesList[position]
                showDeleteDialog(lecture, position)
            }
        }))
        return view
    }

    private fun showDeleteDialog(lecture: Lecture, position: Int) {
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setTitle(resources.getString(R.string.delete))
        dialogBuilder.setMessage(resources.getString(R.string.delete_lecture))
        dialogBuilder.setPositiveButton(resources.getString(R.string.yes)) { dialog, whichButton ->
            AppExecutors.instance.diskIO().execute {
                appDatabase!!.lectureDao().delete(lecture)
                lecturesList.removeAt(position)
                activity!!.runOnUiThread { mLectureAdapter!!.notifyDataSetChanged() }
            }
        }
        dialogBuilder.setNegativeButton(resources.getString(R.string.no), null)
        val b = dialogBuilder.create()
        b.show()
    }
}