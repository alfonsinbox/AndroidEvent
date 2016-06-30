package com.example.alfon.eventtest;

import android.*;
import android.app.Activity;
import android.app.Notification;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Debug;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.List;

public class SignInActivity extends AppCompatActivity {

    Activity activity;
    MobileServiceClient mClient;

    EditText usernameEditText;
    EditText passwordEditText;
    String gcmRegistrationId;

    RelativeLayout signInButton;
    RelativeLayout signingInProgress;

    String encodedImage;
    Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mClient = GlobalApplication.getmClient();
        activity = this;

        // Check if user is logged in
        if (!AuthUtilities.getLocalToken(activity).equals("")) {
            ServiceFilterResponseCallback tokenRefreshedResponseCallback = new ServiceFilterResponseCallback() {
                @Override
                public void onResponse(ServiceFilterResponse response, Exception exception) {
                    if (exception != null) {
                        // User was inactive for too long
                        setContentView(R.layout.activity_sign_in);
                        usernameEditText = (EditText) findViewById(R.id.edittext_username);
                        passwordEditText = (EditText) findViewById(R.id.edittext_password);
                        signInButton = (RelativeLayout) findViewById(R.id.button_sign_in);
                        signingInProgress = (RelativeLayout) findViewById(R.id.signing_in_progress);

                        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                            @Override
                            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                if (actionId == EditorInfo.IME_ACTION_DONE) {
                                    signIn(v);
                                    return true;
                                }
                                return false;
                            }
                        });

                        if (NotificationUtilities.isDeviceRegistered(activity)) {
                            deleteCurrentNotificationRegistration();
                        }

                        return;
                    }
                    String token = new Gson().fromJson(response.getContent(), JsonObject.class).get(GlobalApplication.PREFERENCE_USER_TOKEN).getAsString();
                    AuthUtilities.saveToken(token, activity);

                    navigateCorrectActivityFinishThis();
                }
            };

            // Refresh token
            AuthUtilities.refreshToken(activity, mClient, tokenRefreshedResponseCallback);
        } else {
            setContentView(R.layout.activity_sign_in);

            usernameEditText = (EditText) findViewById(R.id.edittext_username);
            passwordEditText = (EditText) findViewById(R.id.edittext_password);

            signInButton = (RelativeLayout) findViewById(R.id.button_sign_in);
            signingInProgress = (RelativeLayout) findViewById(R.id.signing_in_progress);

            passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        signIn(v);
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    public void deleteCurrentNotificationRegistration() {
        final ServiceFilterResponseCallback deletedNotificationRegistrationResponseCallback = new ServiceFilterResponseCallback() {
            @Override
            public void onResponse(ServiceFilterResponse response, Exception exception) {
                if (exception != null) {
                    exception.printStackTrace();
                    deleteCurrentNotificationRegistration();
                    return;
                }
                NotificationUtilities.setDeviceRegisteredForNotifications(activity, false);
            }
        };
        String registrationId = NotificationUtilities.getRegistrationId(activity);
        NotificationUtilities.deleteRegistration(activity, registrationId, mClient, deletedNotificationRegistrationResponseCallback);
    }

    public void signIn(View view) {
        System.out.println("PREVIOUS TOKEN: " + AuthUtilities.getLocalToken(activity));

        signInButton.setVisibility(View.INVISIBLE);
        signingInProgress.setVisibility(View.VISIBLE);

        final ServiceFilterResponseCallback finishedNotificationRegistrationResponseCallback = new ServiceFilterResponseCallback() {
            @Override
            public void onResponse(ServiceFilterResponse response, Exception exception) {
                if (exception != null) {
                    exception.printStackTrace();
                    signInButton.setVisibility(View.VISIBLE);
                    signingInProgress.setVisibility(View.INVISIBLE);
                    Toast.makeText(activity, R.string.could_not_sign_in_toast, Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(activity, R.string.device_registered_push, Toast.LENGTH_SHORT).show();

                String registrationId = new Gson().fromJson(response.getContent(), JsonObject.class).get("registration_id").getAsString();

                System.out.println(response.getContent());

                NotificationUtilities.setRegistrationId(activity, registrationId);

                NotificationUtilities.setDeviceRegisteredForNotifications(activity, true);

                // If both requests went well, user is logged in
                navigateCorrectActivityFinishThis();
            }
        };

        ServiceFilterResponseCallback gotUserTokenResponseCallback = new ServiceFilterResponseCallback() {
            @Override
            public void onResponse(ServiceFilterResponse response, Exception exception) {
                if (exception != null) {
                    exception.printStackTrace();
                    signInButton.setVisibility(View.VISIBLE);
                    signingInProgress.setVisibility(View.INVISIBLE);
                    Toast.makeText(activity, R.string.could_not_sign_in_toast, Toast.LENGTH_SHORT).show();
                    return;
                }

                String token = new Gson().fromJson(response.getContent(), JsonObject.class).get("token").getAsString();

                try {
                    System.out.println("TOKEN WAS RETRIEVED: " + token);
                    AuthUtilities.saveToken(token, activity);

                    if (!AuthUtilities.getLocalToken(activity).equals("")) {
                        boolean pushAlreadyRegistered = activity.getSharedPreferences(GlobalApplication.PREFERENCES_USERSETTINGS, MODE_PRIVATE).getBoolean(GlobalApplication.PREFERENCE_DEVICE_REGISTERED_FOR_PUSH, false);
                        if (!pushAlreadyRegistered) {
                            // Register device for push notifications
                            gcmRegistrationId = activity
                                    .getSharedPreferences(GlobalApplication.PREFERENCES_USERSETTINGS, Context.MODE_PRIVATE)
                                    .getString(GlobalApplication.PREFERENCE_GCM_REGISTRATION_ID, "");

                            NotificationUtilities.registerGcmId(activity.getApplicationContext(), gcmRegistrationId, GlobalApplication.mClient, finishedNotificationRegistrationResponseCallback);
                        } else {
                            navigateCorrectActivityFinishThis();
                        }
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        AuthUtilities.requestToken(mClient, usernameEditText.getText().toString(), passwordEditText.getText().toString(), gotUserTokenResponseCallback);
    }

    private void navigateCorrectActivityFinishThis() {
        Intent intent;
        String activityRedirect;

        try {
            activityRedirect = getIntent().getExtras().getString(GlobalApplication.NOTIFICATION_EXTRA_ACTIVITY_REDIRECTION);
        } catch (Exception e) {
            activityRedirect = "";
        }

        if (activityRedirect == null) {
            intent = new Intent(activity, MainActivity.class);
        } else {
            switch (activityRedirect) {
                case "UserOverview":
                    intent = new Intent(activity, UserOverviewActivity.class);
                    intent.putExtra(GlobalApplication.EXTRA_USEROVERVIEW_FETCH_DATA,
                            getIntent().getExtras().getBoolean(GlobalApplication.EXTRA_USEROVERVIEW_FETCH_DATA));
                    intent.putExtra(GlobalApplication.EXTRA_USEROVERVIEW_USER_ID,
                            getIntent().getExtras().getString(GlobalApplication.EXTRA_USEROVERVIEW_USER_ID));
                    break;
                case "EventDetails":
                    intent = new Intent(activity, EventDetailsActivity.class);
                    intent.putExtra(GlobalApplication.EXTRA_EVENTDETAILS_FETCH_DATA,
                            getIntent().getExtras().getBoolean(GlobalApplication.EXTRA_EVENTDETAILS_FETCH_DATA));
                    intent.putExtra(GlobalApplication.EXTRA_EVENTDETAILS_EVENT_ID,
                            getIntent().getExtras().getString(GlobalApplication.EXTRA_EVENTDETAILS_EVENT_ID));
                    break;
                default:
                    intent = new Intent(activity, MainActivity.class);
                    break;
            }
        }

        startActivity(intent);
        // Finish activity so you can't navigate back to it
        finish();
    }

    public void navigateForgotPassword(View view) {
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(intent);
    }

    public void navigateCreateAccount(View view) {
        Intent intent = new Intent(this, CreateUserActivity.class);
        startActivity(intent);
    }

    public void startCamera(View view) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo;
        try {
            // place where to store camera taken picture
            photo = createTemporaryFile();
        } catch (Exception e) {
            Toast.makeText(activity, "Please check SD card! Image shot is impossible!", Toast.LENGTH_SHORT).show();
            return;
        }
        mImageUri = Uri.fromFile(photo);
        System.out.println(mImageUri.getEncodedPath());
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        //start camera intent

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            new LongOperation().execute();
        }
    }

    private String postImage(File image) {
        String requestURL = "https://theeventapp.azurewebsites.net/api/image/create?ZUMO-API-VERSION=2.0.0";
        String charset = "UTF-8";
        try {
            MultipartUtility multipart = new MultipartUtility(activity, requestURL, charset);

            multipart.addFilePart("file", image);

            String completeResponse = "";
            List<String> response = multipart.finish();
            System.out.println("SERVER REPLIED:");
            for (String line : response) {
                completeResponse += line;
                System.out.println("Upload Files Response:::" + line);
            }
            return completeResponse;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Something went wrong!";
    }

    private class LongOperation extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return postImage(grabImage(mImageUri));
        }

        @Override
        protected void onPostExecute(String result) {
            System.out.println(result);
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    private File createTemporaryFile() throws Exception {
        File tempDir = getExternalFilesDir("");
        System.out.println("I got an abs path: " + tempDir.getAbsolutePath());
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        return new File(tempDir, "IMG_" + System.currentTimeMillis() + ".jpg");
    }

    public File grabImage(Uri imageUri) {
        try {
            return new File(imageUri.getPath());
        } catch (Exception e) {
            Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

}
