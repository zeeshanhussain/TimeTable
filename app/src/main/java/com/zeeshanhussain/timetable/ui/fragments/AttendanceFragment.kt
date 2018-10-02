package com.zeeshanhussain.timetable.ui.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast

import com.shawnlin.numberpicker.NumberPicker
import com.zeeshanhussain.timetable.R
import com.zeeshanhussain.timetable.adapters.AttendanceAdapter
import com.zeeshanhussain.timetable.model.Subject
import com.zeeshanhussain.timetable.utils.AppExecutors
import com.zeeshanhussain.timetable.utils.DividerItemDecoration
import com.zeeshanhussain.timetable.utils.RecyclerItemClickListener
import com.zeeshanhussain.timetable.viewmodel.MainViewModel

import java.util.ArrayList
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AttendanceFragment : Fragment() {

    private var targetAttendance: Int = 0
    private val subjectsList = ArrayList<Subject>()
    private var listSubjects: RecyclerView? = null
    private var mAttendanceAdapter: AttendanceAdapter? = null
    private var placeholderText: TextView? = null
    private val progressList = ArrayList<Int>()
    private val p = Paint()
    private val options = arrayOf<CharSequence>("Bunk Manager", "Update")
    private var attended: Int = 0
    private var total: Int = 0
    private var mainViewModel: MainViewModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_attendance, container, false)
        val context = context
        var prefs: SharedPreferences? = null

        if (context != null) {
            prefs = PreferenceManager.getDefaultSharedPreferences(getContext())
            targetAttendance = Integer.parseInt(
                    prefs!!.getString(TARGET_ATTENDANCE, DEF_TARGET_ATTENDANCE)!!)
        } else
            targetAttendance = Integer.parseInt(DEF_TARGET_ATTENDANCE)

        listSubjects = view.findViewById(R.id.listAttendance)
        placeholderText = view.findViewById(R.id.attendancePlaceholderText)
        mAttendanceAdapter = AttendanceAdapter(subjectsList, progressList, prefs)
        val mLayoutManager = LinearLayoutManager(activity)
        listSubjects!!.layoutManager = mLayoutManager
        listSubjects!!.itemAnimator = DefaultItemAnimator()
        listSubjects!!.addItemDecoration(DividerItemDecoration(activity!!, LinearLayoutManager.VERTICAL))
        listSubjects!!.adapter = mAttendanceAdapter
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        mainViewModel!!.getSubject().observe(this, Observer { subjects ->
            subjectsList.clear()
            progressList.clear()
            subjectsList.addAll(subjects!!)
            for (i in subjectsList.indices) {
                val progress: Int
                if (subjectsList[i].totalLectures != 0) {
                    progress = subjectsList[i].attendedLectures * 100 / subjectsList[i].totalLectures
                } else {
                    progress = 0
                }
                progressList.add(progress)
                activity!!.runOnUiThread { mAttendanceAdapter!!.notifyDataSetChanged() }
            }
            activity!!.runOnUiThread {
                if (subjectsList.size != 0) {
                    placeholderText!!.visibility = View.GONE
                }
            }
        })
        listSubjects!!.addOnItemTouchListener(RecyclerItemClickListener(getContext()!!, object : RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val subject = subjectsList[position]
                val alertDialog = AlertDialog.Builder(getContext())
                alertDialog.setTitle("Options")
                alertDialog.setItems(options) { dialog, which ->
                    when (which) {
                        0 -> {
                            //check if you can Bunk or not
                            val targetOffset: Int
                            val attendedLectures = subjectsList[position].attendedLectures
                            val totalLectures = subjectsList[position].totalLectures
                            if (attendedLectures == 0) {
                                Toast.makeText(getContext(), "Please update your attendance", Toast.LENGTH_SHORT).show()
                            } else {
                                targetOffset = attendedLectures - totalLectures * targetAttendance / 100
                                if (targetOffset == 0)
                                    Toast.makeText(getContext(), "you can\'t bunk any lectures", Toast.LENGTH_SHORT).show()
                                else {
                                    val plural = if (Math.abs(targetOffset) == 1) "" else "s"
                                    if (targetOffset > 0)
                                        Toast.makeText(getContext(), "you can bunk $targetOffset lecture$plural", Toast.LENGTH_SHORT).show()
                                    if (targetOffset < 0)
                                        Toast.makeText(getContext(), "you need to attend " + -1 * targetOffset + " lecture" + plural, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        1 ->
                            //update Attendance manually
                            showAttendanceDialog(subject, position)
                    }
                }
                alertDialog.show()

            }
        }))
        initSwipe()
        return view
    }

    private fun initSwipe() {
        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                AppExecutors.instance.diskIO().execute {
                    val position = viewHolder.adapterPosition
                    val subjects = subjectsList[position]
                    attended = subjectsList[position].attendedLectures
                    total = subjectsList[position].totalLectures
                    val progress: Int
                    if (direction == ItemTouchHelper.LEFT) {
                        progress = attended * 100 / ++total
                    } else {
                        progress = ++attended * 100 / ++total
                    }
                    subjects.attendedLectures = attended
                    subjects.totalLectures = total
                    mainViewModel!!.updateAttendance(attended, total, subjectsList[position].id)
                    progressList[position] = progress
                    subjectsList[position] = subjects
                    activity!!.runOnUiThread { mAttendanceAdapter!!.notifyDataSetChanged() }
                }


            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {

                val icon: Bitmap
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    val itemView = viewHolder.itemView
                    val height = itemView.bottom.toFloat() - itemView.top.toFloat()
                    val width = height / 3

                    if (dX > 0) {
                        p.color = Color.parseColor("#4CAF50")
                        val background = RectF(itemView.left.toFloat(), itemView.top.toFloat(), dX, itemView.bottom.toFloat())
                        c.drawRect(background, p)
                        icon = BitmapFactory.decodeResource(resources, R.drawable.ic_done)
                        val icon_dest = RectF(itemView.left.toFloat() + width, itemView.top.toFloat() + width, itemView.left.toFloat() + 2 * width, itemView.bottom.toFloat() - width)
                        c.drawBitmap(icon, null, icon_dest, p)
                    } else if (dX < 0) {
                        p.color = Color.parseColor("#F44336")
                        val background = RectF(itemView.right.toFloat() + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
                        c.drawRect(background, p)
                        icon = BitmapFactory.decodeResource(resources, R.drawable.ic_clear)
                        val icon_dest = RectF(itemView.right.toFloat() - 2 * width, itemView.top.toFloat() + width, itemView.right.toFloat() - width, itemView.bottom.toFloat() - width)
                        c.drawBitmap(icon, null, icon_dest, p)
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(listSubjects)
    }

    private fun showAttendanceDialog(subject: Subject, position: Int) {
        val dialogBuilder = AlertDialog.Builder(context)
        val inflater = activity!!.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_edit_attendance, null)
        val attendedLecturesNumberPicker = dialogView.findViewById<NumberPicker>(R.id.attendedLecturesNumberPicker)
        val totalLecturesNumberPicker = dialogView.findViewById<NumberPicker>(R.id.totalLecturesNumberPicker)
        attendedLecturesNumberPicker.value = subject.attendedLectures
        totalLecturesNumberPicker.value = subject.totalLectures
        attendedLecturesNumberPicker.maxValue = totalLecturesNumberPicker.value
        totalLecturesNumberPicker.setOnValueChangedListener { numberPicker, i, i1 -> attendedLecturesNumberPicker.maxValue = totalLecturesNumberPicker.value }
        dialogBuilder.setView(dialogView)
        dialogBuilder.setTitle(resources.getString(R.string.attendance) + ": " + subject.subjectName)
        dialogBuilder.setPositiveButton(resources.getString(R.string.save)) { dialog, whichButton ->
            AppExecutors.instance.diskIO().execute {
                val attendedLectures = attendedLecturesNumberPicker.value
                val totalLectures = totalLecturesNumberPicker.value
                mainViewModel!!.updateAttendance(attendedLectures, totalLectures, subjectsList[position].id)
                val x: Int
                if (attendedLectures == 0 && totalLectures == 0) {
                    x = 0
                } else {
                    x = attendedLectures * 100 / totalLectures
                }
                progressList[position] = x
                subject.attendedLectures = attendedLectures
                subject.totalLectures = totalLectures
                subjectsList[position] = subject
                activity!!.runOnUiThread { mAttendanceAdapter!!.notifyDataSetChanged() }
            }
        }
        dialogBuilder.setNegativeButton(resources.getString(R.string.cancel), null)
        val b = dialogBuilder.create()
        b.show()
    }

    companion object {
        val ATTENDANCE_PREFS = "attendancePrefs"
        private val TARGET_ATTENDANCE = "target_attendance"
        val DEF_TARGET_ATTENDANCE = "75"
    }

}