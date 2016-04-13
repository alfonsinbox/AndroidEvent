package com.example.alfon.eventtest;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateDescriptionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreateDescriptionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateDescriptionFragment extends Fragment {

    private static final String CURRENT_DESCRIPTION = "current description";

    private String mCurrentDescription;

    private OnFragmentInteractionListener mListener;

    public CreateDescriptionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param currentDescription
     * @return A new instance of fragment CreateDescriptionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateDescriptionFragment newInstance(String currentDescription) {
        CreateDescriptionFragment fragment = new CreateDescriptionFragment();
        Bundle args = new Bundle();
        args.putString(CURRENT_DESCRIPTION, currentDescription);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCurrentDescription = getArguments().getString(CURRENT_DESCRIPTION);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_description,
                container, false);
        RelativeLayout buttonFinished = (RelativeLayout) view.findViewById(R.id.finished_adding_description);
        EditText descriptionEditText = (EditText) view.findViewById(R.id.edittext_event_description);

        descriptionEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);

        buttonFinished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishedAddingDescription();
            }
        });
        descriptionEditText.setText(mCurrentDescription);
        descriptionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mCurrentDescription = s.toString();
            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void finishedAddingDescription() {
        if (mListener != null) {
            mListener.onFinishedAddingDescription(mCurrentDescription);
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
        void onFinishedAddingDescription(String description);
    }
}
