package fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.alfonslange.eventbeta.R;

import objects.MainMenuItem;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainMenuFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainMenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainMenuFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public MainMenuFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainMenuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainMenuFragment newInstance(String param1, String param2) {
        MainMenuFragment fragment = new MainMenuFragment();
        Bundle args = new Bundle();
        /*
        * INSERT ARGS HERE
        * */
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            /*
            * RETRIEVE ARGS HERE
            * */
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main_menu, container, false);

        rootView.findViewById(R.id.main_menu_tab_event).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed(MainMenuItem.MENU_EVENT);
            }
        });
        rootView.findViewById(R.id.main_menu_tab_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed(MainMenuItem.MENU_SEARCH);
            }
        });
        rootView.findViewById(R.id.main_menu_tab_create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed(MainMenuItem.MENU_CREATE);
            }
        });
        rootView.findViewById(R.id.main_menu_tab_friends).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed(MainMenuItem.MENU_FRIENDS);
            }
        });
        rootView.findViewById(R.id.main_menu_tab_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed(MainMenuItem.MENU_PROFILE);
            }
        });

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(MainMenuItem pressedMenuItem) {
        if (mListener != null) {
            mListener.onMainMenuItemPressed(pressedMenuItem);
        }
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

    public void changeColoredTab(MainMenuItem oldMenuItem, MainMenuItem newMenuItem){
        TypedValue getValueNotFocused = new TypedValue();
        getResources().getValue(R.dimen.main_menu_alpha_not_focused, getValueNotFocused, true);
        float notFocused = getValueNotFocused.getFloat();

        TypedValue getValueFocused = new TypedValue();
        getResources().getValue(R.dimen.main_menu_alpha_focused, getValueFocused, true);
        float focused = getValueFocused.getFloat();

        Log.d("TAG", "changeColoredTab: focus" + focused);
        Log.d("TAG", "changeColoredTab: no focus" + notFocused);
        switch (oldMenuItem) {
            case MENU_EVENT:
                getView().findViewById(R.id.main_menu_tab_event)
                        .setAlpha(notFocused);
                break;
            case MENU_SEARCH:
                getView().findViewById(R.id.main_menu_tab_search)
                        .setAlpha(notFocused);
                break;
            case MENU_CREATE:
                getView().findViewById(R.id.main_menu_tab_create)
                        .setAlpha(notFocused);
                break;
            case MENU_FRIENDS:
                getView().findViewById(R.id.main_menu_tab_friends)
                        .setAlpha(notFocused);
                break;
            case MENU_PROFILE:
                getView().findViewById(R.id.main_menu_tab_profile)
                        .setAlpha(notFocused);
                break;
        }
        switch (newMenuItem) {
            case MENU_EVENT:
                getView().findViewById(R.id.main_menu_tab_event)
                        .setAlpha(focused);
                break;
            case MENU_SEARCH:
                getView().findViewById(R.id.main_menu_tab_search)
                        .setAlpha(focused);
                break;
            case MENU_CREATE:
                getView().findViewById(R.id.main_menu_tab_create)
                        .setAlpha(focused);
                break;
            case MENU_FRIENDS:
                getView().findViewById(R.id.main_menu_tab_friends)
                        .setAlpha(focused);
                break;
            case MENU_PROFILE:
                getView().findViewById(R.id.main_menu_tab_profile)
                        .setAlpha(focused);
                break;
        }
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
        void onMainMenuItemPressed(MainMenuItem pressedMenuItem);
    }
}
