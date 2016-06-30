package com.example.alfon.eventtest;

import android.app.Activity;
import android.content.Intent;
import android.databinding.BindingConversion;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alfon.eventtest.databinding.ActivityUserOverviewBinding;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UserOverviewActivity extends AppCompatActivity implements MyEventsFragment.OnFragmentInteractionListener, AttendingEventsFragment.OnFragmentInteractionListener {

    Activity activity;
    MobileServiceClient mClient;

    AuthUtilities authUtilities;

    ActivityUserOverviewBinding activityUserOverviewBinding;

    User detailUser;
    RelativeLayout userInterestsRelativeLayout;

    UrlImageView profilePictureImageView;
    Uri uploadImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_overview);

        activity = this;
        mClient = GlobalApplication.getmClient();
        authUtilities = new AuthUtilities();

        activityUserOverviewBinding = DataBindingUtil.setContentView(activity, R.layout.activity_user_overview);

        userInterestsRelativeLayout = (RelativeLayout) findViewById(R.id.user_interests_button);

        profilePictureImageView = (UrlImageView) findViewById(R.id.profile_picture);
        profilePictureImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File photo = FileUtilities.createTemporaryFile(activity, "IMG_" + System.currentTimeMillis() + ".jpg");
                uploadImageUri = Uri.fromFile(photo);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uploadImageUri);

                // Start camera intent if it exists
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, GlobalApplication.REQUEST_CODE_CAMERA_INTENT_PROFILE_PICTURE);
                }

            }
        });

        final ViewPager viewPager = (ViewPager) findViewById(R.id.tab_pages_user_events);
        viewPager.setAdapter(new PagerUserEventsAdapter(getSupportFragmentManager(), 2));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs_user_events);
        // TODO Det ska inte vara hårdkodade strängar!!
        tabLayout.addTab(tabLayout.newTab().setText("Mina event"));
        tabLayout.addTab(tabLayout.newTab().setText("Event att gå på"));
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        loadUser();
    }

    private void loadUser(){
        ServiceFilterResponseCallback userRetrievedResponseCallback = new ServiceFilterResponseCallback() {
            @Override
            public void onResponse(ServiceFilterResponse response, Exception exception) {
                if(exception != null){
                    exception.printStackTrace();
                    return;
                }
                detailUser = new Gson().fromJson(response.getContent(), User.class);
                activityUserOverviewBinding.setUser(detailUser);
                userInterestsRelativeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setUserInterests(v);
                    }
                });

                new DownloadImageTask(profilePictureImageView).execute(detailUser.profilePictureUrl);

                System.out.println(response.getContent());
                System.out.println(detailUser.firstName + " " + detailUser.lastName);
            }
        };

        new UserUtilities().getUserInfo(activity, mClient, userRetrievedResponseCallback);
    }

    public void onFragmentInteraction(Event selectedEvent) {
        Intent intent = new Intent(activity, EventDetailsActivity.class);
        intent.putExtra("event", selectedEvent);
        startActivity(intent);
    }

    @BindingConversion
    public static String birthDateToYears(Date birthDate) {

        if (birthDate != null) {
            return String.valueOf(DateUtilities.differenceBetweenDatesInYears(birthDate, new Date())) + ", ";
        } else {
            return String.valueOf(0) + ", ";
        }
    }

    public void setUserInterests(View view) {
        Intent intent = new Intent(activity, SelectCategoriesActivity.class);
        intent.putExtra("categories", (Serializable) detailUser.interests);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /**
         * requestCode == 0 => selected categories (interests) returned
         */
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 0:
                    ServiceFilterResponseCallback interestAddedResponseCallback = new ServiceFilterResponseCallback() {
                        @Override
                        public void onResponse(ServiceFilterResponse response, Exception exception) {
                            System.out.println(response.getContent());
                            detailUser = new Gson().fromJson(response.getContent(), User.class);
                            activityUserOverviewBinding.setUser(detailUser);
                        }
                    };
                    List<Category> categories = (List<Category>) data.getSerializableExtra("categories");
                    List<String> categoryIds = new ArrayList<>();
                    for (Category c : categories) {
                        categoryIds.add(c.id);
                    }
                    UserUtilities.setInterests(activity, categoryIds, mClient, interestAddedResponseCallback);
                    break;
                case GlobalApplication.REQUEST_CODE_CAMERA_INTENT_PROFILE_PICTURE:
                    UserUtilities.UploadProfilePictureTask uploadProfilePictureTask = new UserUtilities.UploadProfilePictureTask(activity, uploadImageUri, new AsyncTaskCallback() {
                        @Override
                        public void asyncTaskCallbackDone() {
                            loadUser();
                        }
                    });
                    uploadProfilePictureTask.execute();
                    break;
            }
        }
    }

}
