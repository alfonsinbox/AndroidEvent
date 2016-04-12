package com.example.alfon.eventtest;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by alfon on 2016-03-17.
 */
public class PagerUserEventsAdapter extends FragmentPagerAdapter {
    int mNumOfTabs;

    public PagerUserEventsAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                MyEventsFragment tab1 = MyEventsFragment.newInstance();
                return tab1;
            case 1:
                AttendingEventsFragment tab2 = AttendingEventsFragment.newInstance();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

}
