package com.example.alfon.eventtest;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SelectUsersActivity extends AppCompatActivity {

    EditText editTextSearchUsers;
    ListView listViewUsers;
    UserUtilities userUtilities;
    Activity activity;
    MobileServiceClient mClient;

    List<User> selectedUsers;
    ListenableFuture<ServiceFilterResponse> usersListenableFuture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_users);

        listViewUsers = (ListView) findViewById(R.id.listview_users);
        userUtilities = new UserUtilities();
        activity = this;
        mClient = ((GlobalApplication) getApplication()).getmClient();

        selectedUsers = (List<User>) getIntent().getSerializableExtra("users");
        if (selectedUsers == null){
            selectedUsers = new ArrayList<>();
        }

        populateUserList(selectedUsers);

        editTextSearchUsers = (EditText) findViewById(R.id.edittext_search_users);
        editTextSearchUsers.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(usersListenableFuture != null){
                    usersListenableFuture.cancel(true);
                }
                usersListenableFuture = userUtilities.getUsersFromStringFuture(activity, editTextSearchUsers.getText().toString(), mClient);
                Futures.addCallback(usersListenableFuture, new FutureCallback<ServiceFilterResponse>() {
                    @Override
                    public void onSuccess(ServiceFilterResponse result) {
                        List<User> usersResult = new Gson().fromJson(result.getContent(), new TypeToken<List<User>>() {
                        }.getType());
                        populateUserList(usersResult);
                    }

                    @Override
                    public void onFailure(Throwable t) {

                    }
                });
            }
        });
    }

    public void populateUserList(List<User> users){

        for (User user: users) {
            for(User selectedUser: selectedUsers){
                if(selectedUser.id.equals(user.id)){
                    user.user_is_selected = true;
                }
            }
        }

        System.out.println(selectedUsers.size());
        System.out.println(new Gson().toJson(selectedUsers));

        UserSearchAdapter userSearchAdapter = new UserSearchAdapter(activity, R.layout.list_item_user_search, users);
        listViewUsers.setAdapter(userSearchAdapter);
        listViewUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User selectedUser = (User) parent.getItemAtPosition(position);

                for(User user: selectedUsers){
                    if(user.id.equals(selectedUser.id)){
                        ((CheckBox) view.findViewById(R.id.user_is_selected)).setChecked(false);
                        selectedUsers.remove(user);
                        System.out.println(selectedUsers.size());
                        System.out.println(new Gson().toJson(selectedUsers));
                        return;
                    }
                }

                ((CheckBox) view.findViewById(R.id.user_is_selected)).setChecked(true);
                selectedUsers.add(selectedUser);

                System.out.println(selectedUsers.size());
                System.out.println(new Gson().toJson(selectedUsers));
            }
        });
    }

    public void returnSelectedUsers(View view) {
        Intent result = new Intent();
        result.putExtra("users", (Serializable) selectedUsers);
        setResult(Activity.RESULT_OK, result);
        finish();
    }

}
