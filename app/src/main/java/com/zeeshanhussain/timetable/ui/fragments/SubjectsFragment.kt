package com.zeeshanhussain.timetable.ui.fragments

import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView

import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.zeeshanhussain.timetable.R
import com.zeeshanhussain.timetable.adapters.SubjectsAdapter
import com.zeeshanhussain.timetable.model.Subject
import com.zeeshanhussain.timetable.utils.AppExecutors
import com.zeeshanhussain.timetable.utils.DividerItemDecoration
import com.zeeshanhussain.timetable.viewmodel.MainViewModel

import java.util.ArrayList
import java.util.Collections
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SubjectsFragment : Fragment() {

    private val subjectsList = ArrayList<Subject>()
    private var listSubjects: RecyclerView? = null
    private var mSubjectsAdapter: SubjectsAdapter? = null
    private var placeholderText: TextView? = null
    private val p = Paint()
    private var editSubject: AutoCompleteTextView? = null
    private var newSubjectName: AutoCompleteTextView? = null
    private var mainViewModel: MainViewModel? = null

    private var sub: Array<String>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_subjects, container, false)
        listSubjects = view.findViewById(R.id.listSubjects)
        placeholderText = view.findViewById(R.id.subjectsPlaceholderText)
        mSubjectsAdapter = SubjectsAdapter(subjectsList)
        val mLayoutManager = LinearLayoutManager(activity)
        listSubjects!!.layoutManager = mLayoutManager
        listSubjects!!.itemAnimator = DefaultItemAnimator()
        listSubjects!!.addItemDecoration(DividerItemDecoration(activity!!, LinearLayoutManager.VERTICAL))
        listSubjects!!.adapter = mSubjectsAdapter
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        mainViewModel!!.getSubject().observe(this, Observer { subjects ->
            subjectsList.clear()
            subjectsList.addAll(subjects!!)
            Log.d("Size", subjectsList.size.toString())
            mSubjectsAdapter!!.notifyDataSetChanged()
            if (subjectsList.size != 0) {
                placeholderText!!.visibility = View.GONE
            }
        })

        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener { showSubjectDialog() }
        initSwipe()
        return view
    }

    private fun showSubjectDialog() {
        val dialogBuilder = AlertDialog.Builder(activity!!)
        val inflater = activity!!.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_add_subject, null)
        dialogBuilder.setView(dialogView)
        newSubjectName = dialogView.findViewById(R.id.newSubjectName)
        sub = resources.getStringArray(R.array.subjectNames)
        val adapter = ArrayAdapter(activity!!, android.R.layout.simple_list_item_1, sub!!)
        newSubjectName!!.threshold = 2
        newSubjectName!!.setAdapter(adapter)
        dialogBuilder.setTitle(resources.getString(R.string.subject))
        dialogBuilder.setMessage(resources.getString(R.string.enter_subject_name))
        dialogBuilder.setPositiveButton(resources.getString(R.string.add)) { dialog, whichButton ->
            val tempSubject = newSubjectName!!.text.toString().trim { it <= ' ' }
            AppExecutors.instance.diskIO().execute {
                mainViewModel!!.insertSubject(Subject(tempSubject, 0, 0))
                subjectsList.add(Subject(tempSubject, 0, 0))
                Collections.sort(subjectsList, Subject.Comparators.NAME)
                activity!!.runOnUiThread {
                    mSubjectsAdapter!!.notifyDataSetChanged()
                    placeholderText!!.visibility = View.GONE
                    newSubjectName!!.setText("")
                }
            }
        }
        dialogBuilder.setNegativeButton(resources.getString(R.string.cancel), null)
        val dialog = dialogBuilder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
        newSubjectName!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun afterTextChanged(editable: Editable) {
                if (editable.length >= 1) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
                } else {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
                }
            }
        })
    }

    private fun initSwipe() {
        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                if (direction == ItemTouchHelper.LEFT) {
                    deleteSwipe(position)
                } else {
                    initDialog(position)
                }
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {

                val icon: Bitmap
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    val itemView = viewHolder.itemView
                    val height = itemView.bottom.toFloat() - itemView.top.toFloat()
                    val width = height / 3

                    if (dX > 0) {
                        p.color = Color.parseColor("#FF5722")
                        val background = RectF(itemView.left.toFloat(), itemView.top.toFloat(), dX, itemView.bottom.toFloat())
                        c.drawRect(background, p)
                        icon = BitmapFactory.decodeResource(resources, R.drawable.ic_edit_white)
                        val icon_dest = RectF(itemView.left.toFloat() + width, itemView.top.toFloat() + width, itemView.left.toFloat() + 2 * width, itemView.bottom.toFloat() - width)
                        c.drawBitmap(icon, null, icon_dest, p)
                    } else if (dX < 0) {
                        p.color = Color.parseColor("#009688")
                        val background = RectF(itemView.right.toFloat() + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
                        c.drawRect(background, p)
                        icon = BitmapFactory.decodeResource(resources, R.drawable.ic_delete_white)
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

    private fun initDialog(position: Int) {
        val alertDialog = AlertDialog.Builder(activity!!)
        val dialogView = activity!!.layoutInflater.inflate(R.layout.dialog_edit_subject, null)
        editSubject = dialogView.findViewById(R.id.edit_subject)
        sub = resources.getStringArray(R.array.subjectNames)
        val adapte = ArrayAdapter(activity!!, android.R.layout.simple_list_item_1, sub!!)
        if (editSubject != null) {
            editSubject!!.threshold = 2
            editSubject!!.setAdapter(adapte)
        }
        alertDialog.setView(dialogView)
        alertDialog.setTitle(resources.getString(R.string.edit_subject))
        alertDialog.setPositiveButton("Save") { dialog, which ->
            AppExecutors.instance.diskIO().execute {
                mainViewModel!!.updateSubject(editSubject!!.text.toString(), subjectsList[position].id)
                subjectsList[position] = Subject(editSubject!!.text.toString())
                Collections.sort(subjectsList, Subject.Comparators.NAME)
                activity!!.runOnUiThread { mSubjectsAdapter!!.notifyDataSetChanged() }
            }

            dialog.dismiss()
        }
        alertDialog.setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
            mSubjectsAdapter!!.notifyDataSetChanged()
            dialog.dismiss()
        }
        val dialog = alertDialog.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
        editSubject!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun afterTextChanged(editable: Editable) {
                if (editable.length >= 1) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
                    mSubjectsAdapter!!.notifyDataSetChanged()
                } else {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
                    mSubjectsAdapter!!.notifyDataSetChanged()
                }
            }
        })
        mSubjectsAdapter!!.notifyDataSetChanged()
    }

    private fun deleteSwipe(position: Int) {
        val alertDialog = android.app.AlertDialog.Builder(activity)
        alertDialog.setTitle(resources.getString(R.string.warning))
        alertDialog.setMessage(resources.getString(R.string.subject_question))
        alertDialog.setPositiveButton(resources.getString(R.string.yes)) { dialog, which ->
            AppExecutors.instance.diskIO().execute {
                val subject = subjectsList[position]
                mainViewModel!!.deleteSubject(subject)
                subjectsList.removeAt(position)
                activity!!.runOnUiThread { mSubjectsAdapter!!.notifyDataSetChanged() }
            }
        }
        alertDialog.setNegativeButton(resources.getString(R.string.no)) { dialog, which -> mSubjectsAdapter!!.notifyDataSetChanged() }
        alertDialog.show()
        mSubjectsAdapter!!.notifyDataSetChanged()
    }
}