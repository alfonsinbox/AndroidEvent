package adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import fragments.AttendingEventsFragment;
import fragments.MyEventsFragment;

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
                return MyEventsFragment.newInstance();
            case 1:
                return AttendingEventsFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

}
