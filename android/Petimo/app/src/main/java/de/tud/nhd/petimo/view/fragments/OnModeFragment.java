package de.tud.nhd.petimo.view.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.controller.PetimoController;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class OnModeFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private PetimoController controller;

    private TextView textViewMonitoring;
    private TextView textViewCatTask;
    private TextView textViewDate;
    private TextView textViewStartTime;
    private Button buttonStop;

    public OnModeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            controller = PetimoController.getInstance();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        try {
            mListener = (OnFragmentInteractionListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_on_mode, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        textViewMonitoring = (TextView) getView().findViewById(R.id.textViewMonitoring);
        textViewCatTask = (TextView) getView().findViewById(R.id.textViewCatTask);
        textViewDate = (TextView) getView().findViewById(R.id.textViewDate);
        textViewStartTime = (TextView) getView().findViewById(R.id.textViewStartTime);
        buttonStop = (Button) getView().findViewById(R.id.buttonStop);
    }

    @Override
    public void onResume() {
        super.onResume();
        buttonStop.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onStopButtonClicked();
                }
            }
        });

        // Update view
        textViewMonitoring.setText("< " + textViewMonitoring.getText() + " >");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
