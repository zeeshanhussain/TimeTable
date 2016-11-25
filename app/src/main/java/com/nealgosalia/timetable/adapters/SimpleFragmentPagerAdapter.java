package com.nealgosalia.timetable.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.nealgosalia.timetable.R;
import com.nealgosalia.timetable.fragments.FridayFragment;
import com.nealgosalia.timetable.fragments.MondayFragment;
import com.nealgosalia.timetable.fragments.ThursdayFragment;
import com.nealgosalia.timetable.fragments.TuesdayFragment;
import com.nealgosalia.timetable.fragments.WednesdayFragment;


public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {
    private Context mContext;

    public SimpleFragmentPagerAdapter(FragmentManager fm,Context context) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new MondayFragment();
        } else if (position == 1){
            return new TuesdayFragment();
        } else if (position == 2){
            return new WednesdayFragment();
        } else if (position == 3) {
            return  new ThursdayFragment();
        } else {
            return  new FridayFragment();
        }
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return mContext.getString(R.string.monday);
        } else if (position == 1) {
            return mContext.getString(R.string.tuesday);
        } else if (position == 2) {
            return mContext.getString(R.string.wednesday);
        } else if (position == 3){
            return mContext.getString(R.string.thursday);
        } else {
            return  mContext.getString(R.string.friday);
        }
    }



}
