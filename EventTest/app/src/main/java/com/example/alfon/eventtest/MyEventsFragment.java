package com.example.alfon.eventtest;

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

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyEventsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyEventsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyEventsFragment extends Fragment {

    Activity activity;
    MobileServiceClient mClient;
    GridView gridViewEvents;
    ProgressBar progressBarLoadingEvents;

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

        activity = getActivity();

        ServiceFilterResponseCallback eventsRetrievedResponseCallback = new ServiceFilterResponseCallback() {
            @Override
            public void onResponse(ServiceFilterResponse response, Exception exception) {
                if(exception != null){
                    exception.printStackTrace();
                    return;
                }
                progressBarLoadingEvents.setVisibility(View.GONE);
                System.out.println(response.getContent());
                Gson gson = new GsonBuilder().registerTypeAdapter(Calendar.class, new IsoStringToCalendarSerializer()).create();
                List<Event> events = gson.fromJson(response.getContent(), new TypeToken<List<Event>>() {
                }.getType());

                EventsListAdapter eventsListAdapter = new EventsListAdapter(activity, R.layout.grid_item_events, events);
                gridViewEvents.setAdapter(eventsListAdapter);
                gridViewEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (mListener != null) {
                            Event selectedEvent = (Event) parent.getItemAtPosition(position);
                            mListener.onFragmentInteraction(selectedEvent);
                        }
                    }
                });
            }
        };

        EventUtilities.getMyEvents(activity, GlobalApplication.getmClient(), eventsRetrievedResponseCallback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_my_events, container, false);
        gridViewEvents = (GridView) rootView.findViewById(R.id.gridview_events);
        progressBarLoadingEvents = (ProgressBar) rootView.findViewById(R.id.loading_events_progress);
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
        void onFragmentInteraction(Event selectedEvent);
    }
}
