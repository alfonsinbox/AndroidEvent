package com.example.alfonslange.eventbeta;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;

import java.net.MalformedURLException;
import java.util.ArrayList;

import fragments.AttendingEventsFragment;
import fragments.CreateEventFragment;
import fragments.EventDetailsFragment;
import fragments.EventMainFragment;
import fragments.MainMenuFragment;
import fragments.MyEventsFragment;
import fragments.SelectUsersFragment;
import fragments.UserOverviewFragment;
import helpers.NavigationManager;
import helpers.TokenCheck;
import objects.Category;
import objects.Event;
import objects.Globals;
import objects.MainMenuItem;
import objects.RequestCode;
import objects.User;

public class MainActivity extends AppCompatActivity implements
        MainMenuFragment.OnFragmentInteractionListener,
        UserOverviewFragment.OnFragmentInteractionListener,
        EventMainFragment.OnFragmentInteractionListener,
        MyEventsFragment.OnFragmentInteractionListener,
        AttendingEventsFragment.OnFragmentInteractionListener,
        EventDetailsFragment.OnFragmentInteractionListener,
        CreateEventFragment.OnFragmentInteractionListener,
        SelectUsersFragment.OnFragmentInteractionListener {

    private static final String TAG = "MainActivity";

    Activity mActivity;
    MobileServiceClient mClient;

    NavigationManager mNavigationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate: CREATED THE DAMN MAIN ACTIVITY");

        mActivity = this;

        TokenCheck.setContext(this);

        mNavigationManager = new NavigationManager();
        mNavigationManager.init(getSupportFragmentManager());
        mNavigationManager.switchFragmentStack(MainMenuItem.MENU_EVENT);
    }

    public MobileServiceClient getMobileServiceClient() {
        return mClient;
    }

    public NavigationManager getNavigationManager() {
        return mNavigationManager;
    }

    /*
        Native back press in Android
     */
    @Override
    public void onBackPressed() {
        mNavigationManager.navigateBack(mActivity);
    }

    /*
        Implemented from MainMenuFragment
     */
    @Override
    public void onMainMenuItemPressed(MainMenuItem pressedMenuItem) {
        mNavigationManager.switchFragmentStack(pressedMenuItem);
    }

    /*
        Implemented from UserOverviewFragment
     */
    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /*
        Implemented from both MyEventsFragment and AttendingEventsFragment
     */
    @Override
    public void onSelectedEvent(Event selectedEvent) {
        mNavigationManager.createEventDetailsFragment(selectedEvent);
    }

    /*
        Implemented from CreateEventFragment
     */
    @Override
    public void startSelectUsersFragment(ArrayList<User> users, RequestCode requestCode) {
        mNavigationManager.createSelectUsersFragment(users, requestCode);
    }
    @Override
    public void startSelectCategoriesFragment(ArrayList<Category> categories, RequestCode requestCode) {
        //mNavigationManager.createSelectUsersFragment(users, requestCode);
    }

    /*
        Implemented from SelectUsersFragment
     */
    @Override
    public void returnSelectedUsers(ArrayList<User> selectedUsers, RequestCode requestCode) {
        Log.d(TAG, "returnSelectedUsers: From request code: " + requestCode + ", got Users: " + new Gson().toJson(selectedUsers) + "");
    }
}
