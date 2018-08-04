package com.zeeshanhussain.timetable.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.zeeshanhussain.timetable.R;
import com.zeeshanhussain.timetable.ui.fragments.FridayFragment;
import com.zeeshanhussain.timetable.ui.fragments.MondayFragment;
import com.zeeshanhussain.timetable.ui.fragments.SaturdayFragment;
import com.zeeshanhussain.timetable.ui.fragments.SundayFragment;
import com.zeeshanhussain.timetable.ui.fragments.ThursdayFragment;
import com.zeeshanhussain.timetable.ui.fragments.TuesdayFragment;
import com.zeeshanhussain.timetable.ui.fragments.WednesdayFragment;

public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;

    public SimpleFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new MondayFragment();
        } else if (position == 1) {
            return new TuesdayFragment();
        } else if (position == 2) {
            return new WednesdayFragment();
        } else if (position == 3) {
            return new ThursdayFragment();
        } else if (position == 4) {
            return new FridayFragment();
        } else if (position == 5) {
            return new SaturdayFragment();
        } else {
            return new SundayFragment();
        }
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return 7;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return mContext.getString(R.string.monday);
        } else if (position == 1) {
            return mContext.getString(R.string.tuesday);
        } else if (position == 2) {
            return mContext.getString(R.string.wednesday);
        } else if (position == 3) {
            return mContext.getString(R.string.thursday);
        } else if (position == 4) {
            return mContext.getString(R.string.friday);
        } else if (position == 5) {
            return mContext.getString(R.string.saturday);
        } else {
            return mContext.getString(R.string.sunday);
        }
    }
}