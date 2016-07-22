package com.example.alfon.eventtest;

import android.app.Activity;
import android.content.Intent;
import android.databinding.BindingConversion;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.alfon.eventtest.databinding.ActivityUserOverviewBinding;
import com.google.gson.Gson;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserOverviewActivity extends AppCompatActivity implements MyEventsFragment.OnFragmentInteractionListener, AttendingEventsFragment.OnFragmentInteractionListener {

    Activity activity;
    MobileServiceClient mClient;

    AuthUtilities authUtilities;

    ActivityUserOverviewBinding activityUserOverviewBinding;

    User detailUser;
    RelativeLayout userInterestsRelativeLayout;

    ImageView profilePictureImageView;
    Uri originalImageUri;
    Uri uploadImageUri;

    AsyncTaskCallback failedLoadingImageCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_overview);

        activity = this;
        mClient = GlobalApplication.getmClient();

        failedLoadingImageCallback = new AsyncTaskCallback() {
            @Override
            public void asyncTaskCallbackDone(Boolean success) {
                // TODO Set default image...?
            }
        };

        authUtilities = new AuthUtilities();

        activityUserOverviewBinding = DataBindingUtil.setContentView(activity, R.layout.activity_user_overview);

        userInterestsRelativeLayout = (RelativeLayout) findViewById(R.id.user_interests_button);

        profilePictureImageView = (ImageView) findViewById(R.id.profile_picture);
        profilePictureImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File photo = FileUtilities.createTemporaryFile(activity, "IMG_" + System.currentTimeMillis() + ".jpg");
                File squarePhoto = FileUtilities.createTemporaryFile(activity, "SQUARE_IMG_" + System.currentTimeMillis() + ".jpg");
                originalImageUri = Uri.fromFile(photo);
                uploadImageUri = Uri.fromFile(squarePhoto);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, originalImageUri);

                // Start camera intent if it exists
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, GlobalApplication.REQUEST_CODE_CAMERA_INTENT_TAKE_PICTURE);
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

    private void loadUser() {
        ServiceFilterResponseCallback userRetrievedResponseCallback = new ServiceFilterResponseCallback() {
            @Override
            public void onResponse(ServiceFilterResponse response, Exception exception) {
                if (exception != null) {
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

                new DownloadImageTask(profilePictureImageView, failedLoadingImageCallback).execute(detailUser.profilePictureUrl);

                System.out.println(response.getContent());
                System.out.println(detailUser.fullName);
            }
        };

        new UserUtilities().getUserInfo(activity, mClient, userRetrievedResponseCallback);
    }

    public void onFragmentInteraction(Event selectedEvent) {
        Intent intent = new Intent(activity, EventDetailsActivity.class);
        //intent.putExtra("event", selectedEvent);
        intent.putExtra(GlobalApplication.EXTRA_EVENTDETAILS_EVENT_ID, selectedEvent.id);
        intent.putExtra(GlobalApplication.EXTRA_EVENTDETAILS_FETCH_DATA, true);
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
                case GlobalApplication.REQUEST_CODE_CAMERA_INTENT_TAKE_PICTURE:

                    //Intent cropIntent = new Intent("com.android.camera.action.CROP");

                    //cropIntent.setDataAndType(originalImageUri, "image/*");
                    //cropIntent.putExtra("crop", "true");
                    //cropIntent.putExtra("aspectX", 1);
                    //cropIntent.putExtra("aspectY", 1);
                    //cropIntent.putExtra("return-data", true);
                    ////cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, originalImageUri);
                    //startActivityForResult(cropIntent, GlobalApplication.REQUEST_CODE_CAMERA_INTENT_CROP_PICTURE);
                    Crop.of(originalImageUri, uploadImageUri).withAspect(1, 1).start(activity);
                    break;
                case GlobalApplication.REQUEST_CODE_CAMERA_INTENT_CROP_PICTURE:
                    File image = FileUtilities.getImage(uploadImageUri);
                    String filePath = image.getPath();
                    Bitmap bitmap = BitmapFactory.decodeFile(filePath);

                    int x = bitmap.getWidth();
                    int y = bitmap.getHeight();
                    System.out.println(x + "X" + y);

                    bitmap = FileUtilities.rotateBitmap(uploadImageUri.getPath());
                    int dimen = bitmap.getWidth() > bitmap.getHeight() ? bitmap.getHeight() : bitmap.getWidth();
                    bitmap = FileUtilities.getResizedBitmap(bitmap, dimen, dimen);

                    x = bitmap.getWidth();
                    y = bitmap.getHeight();
                    System.out.println(x + "X" + y);

                    //profilePictureImageView.setImageBitmap(bitmap);

                    FileOutputStream out = null;

                    try {
                        out = new FileOutputStream(uploadImageUri.getPath());
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
                        // PNG is a lossless format, the compression factor (100) is ignored
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (out != null) {
                                out.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    uploadCroppedImage();
                    break;
                /*case GlobalApplication.REQUEST_CODE_CAMERA_INTENT_CROP_PICTURE:

                    //File image = FileUtilities.getImage(originalImageUri);
                    //String filePath = image.getPath();
                    //Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                    //profilePictureImageView.setImageBitmap(bitmap);

                    // get the cropped bitmap
                    try {
                        Bundle extras = data.getExtras();
                        Bitmap croppedImage = extras.getParcelable("data");

                        //profilePictureImageView.setImageBitmap(croppedImage);

                        FileOutputStream out = null;

                        try {
                            out = new FileOutputStream(originalImageUri.getPath());
                            croppedImage.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
                            // PNG is a lossless format, the compression factor (100) is ignored
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if (out != null) {
                                    out.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        //File image = FileUtilities.getImage(originalImageUri);
                        //String filePath = image.getPath();
                        //Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                        //profilePictureImageView.setImageBitmap(bitmap);
                    } finally {
                        final UserUtilities.UploadProfilePictureTask uploadProfilePictureTask = new UserUtilities.UploadProfilePictureTask(activity, originalImageUri, new AsyncTaskCallback() {
                            @Override
                            public void asyncTaskCallbackDone() {
                                File fdelete = new File(originalImageUri.getPath());
                                if (fdelete.exists()) {
                                    if (fdelete.delete()) {
                                        System.out.println("FILE DELETED");
                                    } else {
                                        System.out.println("FILE WAS NOT DELETED!!");
                                    }
                                }
                                loadUser();
                            }
                        });
                        uploadProfilePictureTask.execute();
                    }
                    break;*/
            }
        }
    }

    private void uploadCroppedImage() {
        final UserUtilities.UploadProfilePictureTask uploadProfilePictureTask = new UserUtilities.UploadProfilePictureTask(activity, uploadImageUri, new AsyncTaskCallback() {
            @Override
            public void asyncTaskCallbackDone(Boolean success) {
                if(success) {
                    File fdelete = new File(originalImageUri.getPath());
                    if (fdelete.exists()) {
                        if (fdelete.delete()) {
                            System.out.println("FILE DELETED");
                        } else {
                            System.out.println("FILE WAS NOT DELETED!!");
                        }
                    }
                    fdelete = new File(uploadImageUri.getPath());
                    if (fdelete.exists()) {
                        if (fdelete.delete()) {
                            System.out.println("FILE DELETED");
                        } else {
                            System.out.println("FILE WAS NOT DELETED!!");
                        }
                    }
                    loadUser();
                } else {
                    Toast.makeText(activity, "Kunde inte ladda upp bilden", Toast.LENGTH_SHORT).show();
                }
            }
        });
        uploadProfilePictureTask.execute();
    }

}
