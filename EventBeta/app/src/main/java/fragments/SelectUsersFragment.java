package fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import com.example.alfonslange.eventbeta.R;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

import java.util.ArrayList;
import java.util.List;

import adapters.UserSearchAdapter;
import helpers.ListenableFutureCreatedCallback;
import helpers.UserUtilities;
import objects.Globals;
import objects.RequestCode;
import objects.User;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SelectUsersFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SelectUsersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelectUsersFragment extends Fragment {

    private static final String TAG = "SelectUsersFragment";

    private static final String REQUEST_CODE_KEY = "RequestCodeKey";
    private static final String PRESELECTED_USERS_KEY = "UsersKey";

    List<User> mSelectedUsers;
    RequestCode mRequestCode;

    private ListView mListViewUsers;
    private EditText mEditTextSearchUsers;
    private FloatingActionButton mFabFinishedSelectingUsers;

    ListenableFuture<ServiceFilterResponse> mListenableFuture;
    ListenableFutureCreatedCallback mListenableFutureCreatedCallback;

    private OnFragmentInteractionListener mListener;
    private Activity mActivity;

    public SelectUsersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SelectUsersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SelectUsersFragment newInstance(ArrayList<User> users, RequestCode mRequestCode) {
        SelectUsersFragment fragment = new SelectUsersFragment();
        Bundle args = new Bundle();
        args.putSerializable(REQUEST_CODE_KEY, mRequestCode);
        args.putSerializable(PRESELECTED_USERS_KEY, users);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            mRequestCode = (RequestCode)getArguments().getSerializable(REQUEST_CODE_KEY);
            mSelectedUsers = (ArrayList<User>) getArguments().getSerializable(PRESELECTED_USERS_KEY);
        }
        if(mSelectedUsers == null){
            mSelectedUsers = new ArrayList<>();
        }
        mActivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_select_users, container, false);
        instantiateViews(rootView);
        addViewClickListeners(rootView);
        addTextChangeListener(rootView);
        populateUserList(mSelectedUsers);
        return rootView;
    }

    private void instantiateViews(View rootView) {
        mListViewUsers = (ListView)rootView.findViewById(R.id.listview_select_users);
        mEditTextSearchUsers = (EditText) rootView.findViewById(R.id.edittext_search_users);
        mFabFinishedSelectingUsers = (FloatingActionButton) rootView.findViewById(R.id.fab_finished_selecting_users);
    }

    private void addViewClickListeners(View rootView) {
        mFabFinishedSelectingUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnSelectedUsers();
            }
        });
    }
    private void addTextChangeListener(View rootView) {
        mEditTextSearchUsers.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String query = mEditTextSearchUsers.getText().toString();

                if(mListenableFutureCreatedCallback != null){
                    mListenableFutureCreatedCallback.canceled = true;
                }
                if (mListenableFuture != null) {
                    mListenableFuture.cancel(true);
                }
                if (query.equals("")) {
                    mListViewUsers.setAdapter(null);
                    //searchingUsersProgress.setVisibility(View.GONE);
                    return;
                }

                mListenableFutureCreatedCallback = new ListenableFutureCreatedCallback() {
                    @Override
                    public void onSuccess(ListenableFuture<ServiceFilterResponse> listenableFuture) {
                        if(!this.canceled) {

                            mListenableFuture = listenableFuture;
                            Futures.addCallback(mListenableFuture, new FutureCallback<ServiceFilterResponse>() {
                                @Override
                                public void onSuccess(ServiceFilterResponse result) {
                                    List<User> usersResult = new Gson().fromJson(result.getContent(), new TypeToken<List<User>>() {
                                    }.getType());
                                    populateUserList(usersResult);
                                    Log.d(TAG, "onSuccess: Got result " + result.getContent());
                                }

                                @Override
                                public void onFailure(Throwable t) {

                                }
                            });
                        }
                    }
                };

                UserUtilities.getUsersFromStringFuture(mActivity, query, Globals.mClient, mListenableFutureCreatedCallback);
            }
        });

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


    public void populateUserList(List<User> users) {

        for (User user : users) {
            for (User selectedUser : mSelectedUsers) {
                if (selectedUser.id.equals(user.id)) {
                    user.user_is_selected = true;
                }
            }
        }

        Log.d(TAG, "populateUserList: " + mSelectedUsers.size());
        Log.d(TAG, "populateUserList: " + new Gson().toJson(mSelectedUsers));

        final UserSearchAdapter userSearchAdapter = new UserSearchAdapter(mActivity, R.layout.list_item_user_search, users);
        mListViewUsers.setAdapter(userSearchAdapter);
        mListViewUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User selectedUser = (User) parent.getItemAtPosition(position);

                for (User user : mSelectedUsers) {
                    if (user.id.equals(selectedUser.id)) {
                        ((CheckBox) view.findViewById(R.id.user_is_selected)).setChecked(false);
                        Log.d(TAG, "onItemClick: UNCHECKED A BOX");
                        mSelectedUsers.remove(user);
                        Log.d(TAG, "populateUserList: " + mSelectedUsers.size());
                        Log.d(TAG, "populateUserList: " + new Gson().toJson(mSelectedUsers));
                        return;
                    }
                }

                ((CheckBox) view.findViewById(R.id.user_is_selected)).setChecked(true);
                mSelectedUsers.add(selectedUser);

                Log.d(TAG, "onItemClick: CHECKED A BOX");
                Log.d(TAG, "populateUserList: " + mSelectedUsers.size());
                Log.d(TAG, "populateUserList: " + new Gson().toJson(mSelectedUsers));
            }
        });
    }

    public void returnSelectedUsers() {
        mListener.returnSelectedUsers(new ArrayList<>(mSelectedUsers), mRequestCode);
    }

    public interface OnFragmentInteractionListener {
        void returnSelectedUsers(ArrayList<User> selectedUsers, RequestCode requestCode);
    }
}
