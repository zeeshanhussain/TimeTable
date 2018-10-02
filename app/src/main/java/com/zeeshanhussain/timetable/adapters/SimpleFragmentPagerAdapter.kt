package com.zeeshanhussain.timetable.adapters

import android.content.Context

import com.zeeshanhussain.timetable.R
import com.zeeshanhussain.timetable.ui.fragments.FridayFragment
import com.zeeshanhussain.timetable.ui.fragments.MondayFragment
import com.zeeshanhussain.timetable.ui.fragments.SaturdayFragment
import com.zeeshanhussain.timetable.ui.fragments.SundayFragment
import com.zeeshanhussain.timetable.ui.fragments.ThursdayFragment
import com.zeeshanhussain.timetable.ui.fragments.TuesdayFragment
import com.zeeshanhussain.timetable.ui.fragments.WednesdayFragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.PagerAdapter

class SimpleFragmentPagerAdapter(fm: FragmentManager, private val mContext: Context) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return if (position == 0) {
            MondayFragment()
        } else if (position == 1) {
            TuesdayFragment()
        } else if (position == 2) {
            WednesdayFragment()
        } else if (position == 3) {
            ThursdayFragment()
        } else if (position == 4) {
            FridayFragment()
        } else if (position == 5) {
            SaturdayFragment()
        } else {
            SundayFragment()
        }
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    override fun getCount(): Int {
        return 7
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return if (position == 0) {
            mContext.getString(R.string.monday)
        } else if (position == 1) {
            mContext.getString(R.string.tuesday)
        } else if (position == 2) {
            mContext.getString(R.string.wednesday)
        } else if (position == 3) {
            mContext.getString(R.string.thursday)
        } else if (position == 4) {
            mContext.getString(R.string.friday)
        } else if (position == 5) {
            mContext.getString(R.string.saturday)
        } else {
            mContext.getString(R.string.sunday)
        }
    }
}