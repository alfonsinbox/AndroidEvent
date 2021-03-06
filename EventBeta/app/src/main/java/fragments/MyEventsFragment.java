package fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.example.alfonslange.eventbeta.R;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

import java.util.Calendar;
import java.util.List;

import helpers.EventUtilities;
import adapters.EventsListAdapter;
import helpers.IsoStringToCalendarSerializer;
import objects.Event;
import objects.Globals;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyEventsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyEventsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyEventsFragment extends Fragment {
    Activity mActivity;
    GridView mGridViewEvents;
    ProgressBar mProgressBarLoadingEvents;

    private OnFragmentInteractionListener mListener;

    public MyEventsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyEventsFragment.
     */
    public static MyEventsFragment newInstance() {
        MyEventsFragment fragment = new MyEventsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

        mActivity = getActivity();

        ServiceFilterResponseCallback eventsRetrievedResponseCallback = new ServiceFilterResponseCallback() {
            @Override
            public void onResponse(ServiceFilterResponse response, Exception exception) {
                if(exception != null){
                    exception.printStackTrace();
                    return;
                }
                mProgressBarLoadingEvents.setVisibility(View.GONE);
                System.out.println(response.getContent());
                Gson gson = new GsonBuilder().registerTypeAdapter(Calendar.class, new IsoStringToCalendarSerializer()).create();
                List<Event> events = gson.fromJson(response.getContent(), new TypeToken<List<Event>>() {
                }.getType());

                EventsListAdapter eventsListAdapter = new EventsListAdapter(mActivity, R.layout.grid_item_events, events);
                mGridViewEvents.setAdapter(eventsListAdapter);
                mGridViewEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (mListener != null) {
                            Event selectedEvent = (Event) parent.getItemAtPosition(position);
                            mListener.onSelectedEvent(selectedEvent);
                        }
                    }
                });
            }
        };

        EventUtilities.getMyEvents(mActivity, Globals.mClient, eventsRetrievedResponseCallback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_my_events, container, false);
        mGridViewEvents = (GridView) rootView.findViewById(R.id.gridview_events);
        mProgressBarLoadingEvents = (ProgressBar) rootView.findViewById(R.id.loading_events_progress);
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
        void onSelectedEvent(Event selectedEvent);
    }
}
