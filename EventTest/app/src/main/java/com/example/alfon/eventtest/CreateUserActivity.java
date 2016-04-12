package com.example.alfon.eventtest;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

import java.util.Calendar;
import java.util.Date;

public class CreateUserActivity extends AppCompatActivity {

    Activity activity;
    MobileServiceClient mClient;

    EditText editTextFirstName;
    EditText editTextLastName;
    EditText editTextUsername;
    EditText editTextEmail;
    EditText editTextPassword;

    RelativeLayout createUserButton;
    RelativeLayout creatingUserProgress;

    User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        activity = this;
        mClient = ((GlobalApplication) getApplication()).getmClient();

        editTextFirstName = ((EditText) findViewById(R.id.edittext_firstname));
        editTextLastName = ((EditText) findViewById(R.id.edittext_lastname));
        editTextUsername = ((EditText) findViewById(R.id.edittext_username));
        editTextEmail = ((EditText) findViewById(R.id.edittext_email));
        editTextPassword = ((EditText) findViewById(R.id.edittext_password));

        createUserButton = ((RelativeLayout) findViewById(R.id.create_user_button));
        creatingUserProgress = ((RelativeLayout) findViewById(R.id.creating_user_progress));
    }

    public void createAccount(View view) {
        try {
            ServiceFilterResponseCallback accountCreatedCallback = new ServiceFilterResponseCallback() {
                @Override
                public void onResponse(ServiceFilterResponse response, Exception exception) {
                    if(exception != null) {
                        exception.printStackTrace();
                        createUserButton.setVisibility(View.VISIBLE);
                        creatingUserProgress.setVisibility(View.GONE);
                        return;
                    }
                    System.out.println(response.getContent());

                    String token = new Gson().fromJson(response.getContent(), JsonObject.class).get("token").getAsString();
                    AuthUtilities.saveToken(token, activity);

                    if (!AuthUtilities.getLocalToken(activity).equals("")) {
                        Intent intent = new Intent(activity, MainActivity.class);
                        startActivity(intent);
                        // Finish activity so you can't navigate back to it
                        finish();
                    } else {

                    }
                }
            };

            user.firstName = editTextFirstName.getText().toString();
            user.lastName = editTextLastName.getText().toString();
            user.username = editTextUsername.getText().toString();
            user.password = editTextPassword.getText().toString();
            user.email = editTextEmail.getText().toString();
            Gson gson = new GsonBuilder()
                    .registerTypeHierarchyAdapter(Calendar.class, new IsoStringToCalendarSerializer())
                    .create();
            System.out.println(gson.toJson(user));

            createUserButton.setVisibility(View.GONE);
            creatingUserProgress.setVisibility(View.VISIBLE);

            UserUtilities.createUser(activity, gson.toJson(user), mClient, accountCreatedCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void selectBirthDate(View view){

        Calendar nowCalendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendarToSet = Calendar.getInstance();
                calendarToSet.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
                user.birthDate = calendarToSet;
                ((TextView)findViewById(R.id.select_birthdate)).setText(DateUtilities.dateToStringFormat(calendarToSet, "dd MMMM, yyyy"));
            }
        },
                nowCalendar.get(Calendar.YEAR),
                nowCalendar.get(Calendar.MONTH),
                nowCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();

    }
}
