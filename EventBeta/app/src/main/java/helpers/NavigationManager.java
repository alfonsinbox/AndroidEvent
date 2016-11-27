package helpers;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.example.alfonslange.eventbeta.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import fragments.CreateEventFragment;
import fragments.EventDetailsFragment;
import fragments.EventMainFragment;
import fragments.MainMenuFragment;
import fragments.SelectUsersFragment;
import fragments.UserOverviewFragment;
import objects.Event;
import objects.MainMenuItem;
import objects.RequestCode;
import objects.User;

/**
 * Created by alfonslange on 12/09/16.
 */
public class NavigationManager {


    /**
     * Listener interface for navigation events.
     */
    public interface NavigationListener {

        /**
         * Callback on backstack changed.
         */
        void onBackstackChanged();
    }


    // ------------------------------------------------------------------------
    // STATIC FIELDS
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // STATIC METHODS
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // FIELDS
    // ------------------------------------------------------------------------

    final static String TAG = "NavigationManager";

    private FragmentManager mFragmentManager;
    private NavigationListener mNavigationListener;
    private MainMenuItem mCurrentTabSelected;
    private HashMap<MainMenuItem, Stack<Fragment>> mFragmentStacks;

    // ------------------------------------------------------------------------
    // CONSTRUCTORS
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // METHODS
    // ------------------------------------------------------------------------

    /**
     * Initialize the NavigationManager with a FragmentManager, which will be used at the
     * fragment transactions.
     *
     * @param fragmentManager
     */
    public void init(FragmentManager fragmentManager) {
        initFragmentStacks();
        mFragmentManager = fragmentManager;
        mCurrentTabSelected = MainMenuItem.INITIAL_PLACEHOLDER_TAB;
        /*mFragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (mNavigationListener != null) {
                    mNavigationListener.onBackstackChanged();
                }
            }
        });*/
    }

    private void initFragmentStacks() {
        mFragmentStacks = new HashMap<>();
        for (MainMenuItem menuType : MainMenuItem.values()) {
            mFragmentStacks.put(menuType, new Stack<Fragment>());
        }
    }


    private void setCurrentSelectedTab(MainMenuItem selectedTab) {
        if (selectedTab != mCurrentTabSelected) {
            changeAlphaCurrentSelectedTab(mCurrentTabSelected, selectedTab);
        }

        mCurrentTabSelected = selectedTab;
    }


    private void popFragment() {
        mFragmentManager.executePendingTransactions();
        final Stack<Fragment> stackFragment = mFragmentStacks.get(mCurrentTabSelected);
        if (stackFragment.size() > 1) {
            Fragment fragment = stackFragment.elementAt(stackFragment.size() - 2);
            if (fragment != null) {
                FragmentTransaction ft = mFragmentManager.beginTransaction();
                ft.setCustomAnimations(R.anim.custom_slide_in_right, R.anim.custom_slide_out_right);
                ft.replace(R.id.main_fragment_container, fragment);
                ft.commit();
            }
            stackFragment.pop();
        }
    }

    private void pushFragment(Fragment fragment, boolean addToStack, boolean animate) {
        // Ignore addToStack for now?
        if (addToStack) {
            mFragmentStacks.get(mCurrentTabSelected).push(fragment);
        }
        if (mFragmentManager != null) {
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            if (animate) {
                transaction.setCustomAnimations(R.anim.custom_slide_in_left, R.anim.custom_slide_out_left);
            }
            transaction.replace(R.id.main_fragment_container, fragment)
                    .commit();
        }
    }

    /**
     * Navigates back by popping the back stack. If there is no more items left we finish the current activity.
     *
     * @param baseActivity
     */
    public void navigateBack(Activity baseActivity) {

        Log.d(TAG, "navigateBack: " + mFragmentStacks.get(mCurrentTabSelected).size());

        if (mFragmentStacks.get(mCurrentTabSelected).size() <= 1) {
            // we can finish the base activity since we have no other fragments
            baseActivity.finish();
        } else {
            popFragment();
        }
    }

    public void switchFragmentStack(MainMenuItem mainMenuItem) {

        Stack<Fragment> thisFragmentStack = mFragmentStacks.get(mainMenuItem);
        Fragment fragment = new Fragment();
        boolean addFragmentToStack = true;

        switch (mainMenuItem) {
            case MENU_EVENT:
                if (thisFragmentStack.size() == 0) {
                    Log.d(TAG, "switchFragmentStack: Created fragment EventMain");
                    fragment = EventMainFragment.newInstance("", "");
                    addFragmentToStack = true;
                } else {
                    fragment = thisFragmentStack.lastElement();
                    addFragmentToStack = false;
                }
                break;
            case MENU_SEARCH:
                break;
            case MENU_CREATE:
                if (thisFragmentStack.size() == 0) {
                    Log.d(TAG, "switchFragmentStack: Created fragment CreateEvent");
                    fragment = CreateEventFragment.newInstance();
                    addFragmentToStack = true;
                } else {
                    fragment = thisFragmentStack.lastElement();
                    addFragmentToStack = false;
                }
                break;
            case MENU_FRIENDS:
                break;
            case MENU_PROFILE:
                if (thisFragmentStack.size() == 0) {
                    Log.d(TAG, "switchFragmentStack: Created fragment UserOverview");
                    fragment = UserOverviewFragment.newInstance("", "");
                    addFragmentToStack = true;
                } else {
                    fragment = thisFragmentStack.lastElement();
                    addFragmentToStack = false;
                }
                //startMenu1();
                break;
        }

        setCurrentSelectedTab(mainMenuItem);

        pushFragment(fragment, addFragmentToStack, false);

    }

    public void createEventDetailsFragment(Event event) {
        Fragment fragment = EventDetailsFragment.newInstance(event);
        pushFragment(fragment, true, true);
    }
    public void createSelectUsersFragment(ArrayList<User> users, RequestCode requestCode) {
        Fragment fragment = SelectUsersFragment.newInstance(users, requestCode);
        pushFragment(fragment, true, true);
    }

    private void changeAlphaCurrentSelectedTab(MainMenuItem oldMenuItem, MainMenuItem newMenuItem) {
        ((MainMenuFragment) mFragmentManager
                .findFragmentById(R.id.main_menu_fragment))
                .changeColoredTab(oldMenuItem, newMenuItem);
    }

    /**
     * @return true if the current fragment displayed is a root fragment
     */
    public boolean isRootFragmentVisible() {
        return mFragmentManager.getBackStackEntryCount() <= 1;
    }

    public NavigationListener getNavigationListener() {
        return mNavigationListener;
    }

    public void setNavigationListener(NavigationListener navigationListener) {
        mNavigationListener = navigationListener;
    }
}
