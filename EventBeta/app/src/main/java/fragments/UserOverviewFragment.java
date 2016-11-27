package fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.alfonslange.eventbeta.MainActivity;
import com.example.alfonslange.eventbeta.R;
import com.example.alfonslange.eventbeta.databinding.FragmentUserOverviewBinding;
import com.google.gson.Gson;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

import helpers.DownloadImageTask;
import helpers.IDownloadImageCallback;
import adapters.PagerUserEventsAdapter;
import helpers.UserUtilities;
import objects.Globals;
import objects.User;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserOverviewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserOverviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserOverviewFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "UserOverviewFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private User mDetailUser;
    private FragmentUserOverviewBinding mUserBinding;

    private MainActivity mActivity;

    private OnFragmentInteractionListener mListener;

    ImageView mProfilePictureImageView;

    public UserOverviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserOverviewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserOverviewFragment newInstance(String param1, String param2) {
        UserOverviewFragment fragment = new UserOverviewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            try {
                Log.d(TAG, "onCreate: RANDOM_KEY : " + getArguments().getString("RANDOM_KEY"));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        mActivity = ((MainActivity) getActivity());

        loadUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mUserBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_overview, container, false);
        View rootView = mUserBinding.getRoot();
        mProfilePictureImageView = (ImageView) rootView.findViewById(R.id.profile_picture);

        final ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.tab_pages_user_events);
        viewPager.setAdapter(new PagerUserEventsAdapter(getChildFragmentManager(), 2));

        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tabs_user_events);
        // TODO Det ska inte vara hårdkodade strängar!!
        tabLayout.addTab(tabLayout.newTab().setText("Mina event"));
        tabLayout.addTab(tabLayout.newTab().setText("Event att gå på"));
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("RANDOM_KEY", "RANDOM_VALUE");
        super.onSaveInstanceState(outState);
    }

    private void loadUser() {
        ServiceFilterResponseCallback userRetrievedResponseCallback = new ServiceFilterResponseCallback() {
            @Override
            public void onResponse(ServiceFilterResponse response, Exception exception) {
                if (exception != null) {
                    exception.printStackTrace();
                    return;
                }
                mDetailUser = new Gson().fromJson(response.getContent(), User.class);

                mUserBinding.setUser(mDetailUser);
//                userInterestsRelativeLayout.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        setUserInterests(v);
//                    }
//                });

                IDownloadImageCallback imageLoadedCallback = new IDownloadImageCallback() {
                    @Override
                    public void onSuccess(Bitmap bitmap) {
                        mProfilePictureImageView.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        e.printStackTrace();
                    }
                };

                new DownloadImageTask(imageLoadedCallback).execute(mDetailUser.profilePictureUrl);

                Log.d(TAG, "onResponse: " + response.getContent());
            }
        };

        UserUtilities.getUserInfo(mActivity, Globals.mClient, userRetrievedResponseCallback);
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

//        import android.app.Activity;
//        import android.content.BroadcastReceiver;
//        import android.content.Context;
//        import android.content.Intent;
//        import android.content.IntentFilter;
//        import android.databinding.BindingConversion;
//        import android.databinding.DataBindingUtil;
//        import android.graphics.Bitmap;
//        import android.graphics.BitmapFactory;
//        import android.net.Uri;
//        import android.provider.MediaStore;
//        import android.support.design.widget.TabLayout;
//        import android.support.v4.view.ViewPager;
//        import android.support.v7.app.AppCompatActivity;
//        import android.os.Bundle;
//        import android.view.View;
//        import android.widget.ImageView;
//        import android.widget.RelativeLayout;
//        import android.widget.Toast;
//
//        import com.example.alfon.eventtest.databinding.ActivityUserOverviewBinding;
//        import com.google.gson.Gson;
//        import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
//        import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
//        import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
//        import com.soundcloud.android.crop.Crop;
//
//        import java.io.File;
//        import java.io.FileOutputStream;
//        import java.io.IOException;
//        import java.io.Serializable;
//        import java.util.ArrayList;
//        import java.util.Date;
//        import java.util.List;
//
//        Activity activity;
//
//        AuthUtilities authUtilities;
//
//        ActivityUserOverviewBinding activityUserOverviewBinding;
//
//        User detailUser;
//        RelativeLayout userInterestsRelativeLayout;
//
//        ImageView profilePictureImageView;
//        Uri originalImageUri;
//        Uri uploadImageUri;
//
//        AsyncTaskCallback failedLoadingImageCallback;
//
//@Override
//protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_user_overview);
//
//        activity = this;
//        mClient = GlobalApplication.getmClient();
//
//        failedLoadingImageCallback = new AsyncTaskCallback() {
//@Override
//public void asyncTaskCallbackDone(Boolean success) {
//        // TODO Set default image...?
//        }
//        };
//
//        authUtilities = new AuthUtilities();
//
//        activityUserOverviewBinding = DataBindingUtil.setContentView(activity, R.layout.activity_user_overview);
//
//        userInterestsRelativeLayout = (RelativeLayout) findViewById(R.id.user_interests_button);
//
//        profilePictureImageView = (ImageView) findViewById(R.id.profile_picture);
//        profilePictureImageView.setOnClickListener(new View.OnClickListener() {
//@Override
//public void onClick(View v) {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        File photo = FileUtilities.createTemporaryFile(activity, "IMG_" + System.currentTimeMillis() + ".jpg");
//        File squarePhoto = FileUtilities.createTemporaryFile(activity, "SQUARE_IMG_" + System.currentTimeMillis() + ".jpg");
//        originalImageUri = Uri.fromFile(photo);
//        uploadImageUri = Uri.fromFile(squarePhoto);
//        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, originalImageUri);
//
//        // Start camera intent if it exists
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//        startActivityForResult(takePictureIntent, GlobalApplication.REQUEST_CODE_CAMERA_INTENT_TAKE_PICTURE);
//        }
//
//        }
//        });
//
//final ViewPager viewPager = (ViewPager) findViewById(R.id.tab_pages_user_events);
//        viewPager.setAdapter(new PagerUserEventsAdapter(getSupportFragmentManager(), 2));
//
//        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs_user_events);
//        // TODO Det ska inte vara hårdkodade strängar!!
//        tabLayout.addTab(tabLayout.newTab().setText("Mina event"));
//        tabLayout.addTab(tabLayout.newTab().setText("Event att gå på"));
//        tabLayout.setTabMode(TabLayout.MODE_FIXED);
//        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
//
//        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
//        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//@Override
//public void onTabSelected(TabLayout.Tab tab) {
//        viewPager.setCurrentItem(tab.getPosition());
//        }
//
//@Override
//public void onTabUnselected(TabLayout.Tab tab) {
//
//        }
//
//@Override
//public void onTabReselected(TabLayout.Tab tab) {
//
//        }
//        });
//
//        loadUser();
//        }
//
//private void loadUser() {
//        ServiceFilterResponseCallback userRetrievedResponseCallback = new ServiceFilterResponseCallback() {
//@Override
//public void onResponse(ServiceFilterResponse response, Exception exception) {
//        if (exception != null) {
//        exception.printStackTrace();
//        return;
//        }
//        detailUser = new Gson().fromJson(response.getContent(), User.class);
//        activityUserOverviewBinding.setUser(detailUser);
//        userInterestsRelativeLayout.setOnClickListener(new View.OnClickListener() {
//@Override
//public void onClick(View v) {
//        setUserInterests(v);
//        }
//        });
//
//        new DownloadImageTask(profilePictureImageView, failedLoadingImageCallback).execute(detailUser.profilePictureUrl);
//
//        System.out.println(response.getContent());
//        System.out.println(detailUser.fullName);
//        }
//        };
//
//        new UserUtilities().getUserInfo(activity, mClient, userRetrievedResponseCallback);
//        }
//
//public void onFragmentInteraction(Event selectedEvent) {
//        Intent intent = new Intent(activity, EventDetailsActivity.class);
//        //intent.putExtra("event", selectedEvent);
//        intent.putExtra(GlobalApplication.EXTRA_EVENTDETAILS_EVENT_ID, selectedEvent.id);
//        intent.putExtra(GlobalApplication.EXTRA_EVENTDETAILS_FETCH_DATA, true);
//        startActivity(intent);
//        }
//
//@BindingConversion
//public static String birthDateToYears(Date birthDate) {
//        if (birthDate != null) {
//        return String.valueOf(DateUtilities.differenceBetweenDatesInYears(birthDate, new Date())) + ", ";
//        } else {
//        return String.valueOf(0) + ", ";
//        }
//        }
//
//public void setUserInterests(View view) {
//        Intent intent = new Intent(activity, SelectCategoriesActivity.class);
//        intent.putExtra("categories", (Serializable) detailUser.interests);
//        startActivityForResult(intent, 0);
//        }
//
//@Override
//protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        /**
//         * requestCode == 0 => selected categories (interests) returned
//         */
//        if (resultCode == Activity.RESULT_OK) {
//        switch (requestCode) {
//        case 0:
//        ServiceFilterResponseCallback interestAddedResponseCallback = new ServiceFilterResponseCallback() {
//@Override
//public void onResponse(ServiceFilterResponse response, Exception exception) {
//        System.out.println(response.getContent());
//        detailUser = new Gson().fromJson(response.getContent(), User.class);
//        activityUserOverviewBinding.setUser(detailUser);
//        }
//        };
//        List<Category> categories = (List<Category>) data.getSerializableExtra("categories");
//        List<String> categoryIds = new ArrayList<>();
//        for (Category c : categories) {
//        categoryIds.add(c.id);
//        }
//        UserUtilities.setInterests(activity, categoryIds, mClient, interestAddedResponseCallback);
//        break;
//        case GlobalApplication.REQUEST_CODE_CAMERA_INTENT_TAKE_PICTURE:
//
//        Crop.of(originalImageUri, uploadImageUri).withAspect(1, 1).start(activity);
//        break;
//        case GlobalApplication.REQUEST_CODE_CAMERA_INTENT_CROP_PICTURE:
//        File image = FileUtilities.getImage(uploadImageUri);
//        String filePath = image.getPath();
//        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
//
//        int x = bitmap.getWidth();
//        int y = bitmap.getHeight();
//        System.out.println(x + "X" + y);
//
//        bitmap = FileUtilities.rotateBitmap(uploadImageUri.getPath());
//        int dimen = bitmap.getWidth() > bitmap.getHeight() ? bitmap.getHeight() : bitmap.getWidth();
//        bitmap = FileUtilities.getResizedBitmap(bitmap, dimen, dimen);
//
//        x = bitmap.getWidth();
//        y = bitmap.getHeight();
//        System.out.println(x + "X" + y);
//
//        //profilePictureImageView.setImageBitmap(bitmap);
//
//        FileOutputStream out = null;
//
//        try {
//        out = new FileOutputStream(uploadImageUri.getPath());
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
//        // PNG is a lossless format, the compression factor (100) is ignored
//        } catch (Exception e) {
//        e.printStackTrace();
//        } finally {
//        try {
//        if (out != null) {
//        out.close();
//        }
//        } catch (IOException e) {
//        e.printStackTrace();
//        }
//        }
//        uploadCroppedImage();
//        break;
//        }
//        }
//        }
//
//private void uploadCroppedImage() {
//final UserUtilities.UploadProfilePictureTask uploadProfilePictureTask = new UserUtilities.UploadProfilePictureTask(activity, uploadImageUri, new AsyncTaskCallback() {
//@Override
//public void asyncTaskCallbackDone(Boolean success) {
//        if(success) {
//        File fdelete = new File(originalImageUri.getPath());
//        if (fdelete.exists()) {
//        if (fdelete.delete()) {
//        System.out.println("FILE DELETED");
//        } else {
//        System.out.println("FILE WAS NOT DELETED!!");
//        }
//        }
//        fdelete = new File(uploadImageUri.getPath());
//        if (fdelete.exists()) {
//        if (fdelete.delete()) {
//        System.out.println("FILE DELETED");
//        } else {
//        System.out.println("FILE WAS NOT DELETED!!");
//        }
//        }
//        loadUser();
//        } else {
//        Toast.makeText(activity, "Kunde inte ladda upp bilden", Toast.LENGTH_SHORT).show();
//        }
//        }
//        });
//
//        BroadcastReceiver receiver = new BroadcastReceiver() {
//@Override
//public void onReceive(Context context, Intent intent) {
//        activity.unregisterReceiver(this);
//
//        /** This request uses the access token and must therefore
//         *  be executed only when the token has been validated
//         */
//        uploadProfilePictureTask.execute();
//        }
//        };
//        IntentFilter intentFilter = new IntentFilter(GlobalApplication.ACTION_TOKEN_CHECK_CALLBACK_SUCCESS);
//        activity.registerReceiver(receiver, intentFilter);
//        Intent serviceIntent = new Intent(activity, TokenCheckService.class);
//        activity.startService(serviceIntent);
//        }
//
//        }

