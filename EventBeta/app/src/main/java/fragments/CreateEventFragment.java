package fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.alfonslange.eventbeta.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import helpers.DateUtilities;
import objects.Category;
import objects.RequestCode;
import objects.Event;
import objects.User;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateEventFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreateEventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateEventFragment extends Fragment {

    private static final String TAG = "CreateEventFragment";

    Event mEventToCreate;
    Calendar mNowCalendar;
    //Event createdEvent;

    List<User> mSelectedHosts;
    List<User> selectedInvitees;

    private OnFragmentInteractionListener mListener;
    Activity mActivity;

    private TextView EventBeginsValue;
    private TextView EventEndsValue;
    private TextView EventLocationValue;
    private TextView EventDescriptionValue;
    private TextView EventCategoriesValue;
    private TextView EventInviteesValue;
    private TextView EventHostsValue;
    private Switch EventIsPrivateValue;

    public CreateEventFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CreateEventFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateEventFragment newInstance() {
        CreateEventFragment fragment = new CreateEventFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: CREATE");
        if (getArguments() != null) {
        }

        mActivity = getActivity();

        mEventToCreate = new Event();
        mNowCalendar = Calendar.getInstance();
        mSelectedHosts = new ArrayList<>();
        selectedInvitees = new ArrayList<>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreate: CREATE VIEW");
        View rootView = inflater.inflate(R.layout.fragment_create_event, container, false);
        addViewClickListeners(rootView);
        instantiateViews(rootView);
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

    private void instantiateViews(View rootView) {
        EventBeginsValue = ((TextView) rootView.findViewById(R.id.create_event_begins_value));
        EventEndsValue = ((TextView) rootView.findViewById(R.id.create_event_ends_value));
        EventLocationValue = ((TextView) rootView.findViewById(R.id.create_event_location_value));
        EventDescriptionValue = ((TextView) rootView.findViewById(R.id.create_event_description_value));
        EventCategoriesValue = ((TextView) rootView.findViewById(R.id.create_event_categories_value));
        EventInviteesValue = ((TextView) rootView.findViewById(R.id.create_event_invitees_value));
        EventHostsValue = ((TextView) rootView.findViewById(R.id.create_event_hosts_value));
        EventIsPrivateValue = ((Switch) rootView.findViewById(R.id.create_event_is_private_value));
    }

    public void addViewClickListeners(View rootView) {
        rootView.findViewById(R.id.create_event_begins_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectStartDate();
            }
        });
        rootView.findViewById(R.id.create_event_ends_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectEndDate();
            }
        });
        rootView.findViewById(R.id.create_event_location_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPlace();
            }
        });
        rootView.findViewById(R.id.create_event_description_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDescription();
            }
        });
        rootView.findViewById(R.id.create_event_categories_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectCategories();
            }
        });
        rootView.findViewById(R.id.create_event_invitees_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectInvitees();
            }
        });
        rootView.findViewById(R.id.create_event_hosts_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectHosts();
            }
        });
        rootView.findViewById(R.id.create_event_is_private_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleEventPrivateSwitch();
            }
        });
    }


    public void selectStartDate() {
        final Calendar calendarToSet = Calendar.getInstance();

        final TimePickerDialog timePickerDialog = new TimePickerDialog(mActivity, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendarToSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendarToSet.set(Calendar.MINUTE, minute);

                mEventToCreate.startTime = calendarToSet;
                EventBeginsValue.setText(
                        DateUtilities.dateToStringFormat(mEventToCreate.startTime, "dd MMM (EEE) HH:mm"));
            }
        },
                mNowCalendar.get(Calendar.HOUR_OF_DAY),
                mNowCalendar.get(Calendar.MINUTE),
                true);

        DatePickerDialog datePickerDialog = new DatePickerDialog(mActivity, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendarToSet.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
                timePickerDialog.show();
            }
        },
                mNowCalendar.get(Calendar.YEAR),
                mNowCalendar.get(Calendar.MONTH),
                mNowCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    public void selectEndDate() {
        final Calendar calendarToSet = Calendar.getInstance();

        final TimePickerDialog timePickerDialog = new TimePickerDialog(mActivity, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendarToSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendarToSet.set(Calendar.MINUTE, minute);

                mEventToCreate.endTime = calendarToSet;
                EventEndsValue.setText(
                        DateUtilities.dateToStringFormat(mEventToCreate.endTime, "dd MMM (EEE) HH:mm"));
            }
        },
                mNowCalendar.get(Calendar.HOUR_OF_DAY),
                mNowCalendar.get(Calendar.MINUTE),
                true);

        DatePickerDialog datePickerDialog = new DatePickerDialog(mActivity, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendarToSet.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
                timePickerDialog.show();
            }
        },
                mNowCalendar.get(Calendar.YEAR),
                mNowCalendar.get(Calendar.MONTH),
                mNowCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    public void selectPlace() {
        /*Intent selectPlaceIntent = new Intent(this, SelectLocationActivity.class);
        selectPlaceIntent.putExtra("location", (Serializable) mEventToCreate.location);
        startActivityForResult(selectPlaceIntent, 0);*/
    }

    public void selectHosts() {
        mListener.startSelectUsersFragment(null, RequestCode.CREATE_EVENT_SELECT_HOSTS);
    }

    public void selectInvitees() {
        mListener.startSelectUsersFragment(null, RequestCode.CREATE_EVENT_SELECT_INVITEES);
//        Intent selectHostsIntent = new Intent(this, SelectUsersActivity.class);
//        selectHostsIntent.putExtra("users", (Serializable) selectedInvitees);
//        startActivityForResult(selectHostsIntent, 3);
    }

    public void selectCategories() {
        mListener.startSelectCategoriesFragment(null, RequestCode.CREATE_EVENT_SELECT_CATEGORIES);
    }

    public void addDescription() {
        //mListener.startSelectCategoriesFragment();
    }

    public void toggleEventPrivateSwitch(){
        EventIsPrivateValue.toggle();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void startSelectUsersFragment(ArrayList<User> users, RequestCode requestCode);
        void startSelectCategoriesFragment(ArrayList<Category> categories, RequestCode requestCode);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: STOP");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: DESTROY");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState: SAVING INSTANCE STATE");
    }
}
