package de.tud.nhd.petimo.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.controller.PetimoController;
import de.tud.nhd.petimo.view.fragments.dialogs.ConfirmStopDialogFragment;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnModeFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ModeOnFragment extends Fragment {

    private final String TAG = "ModeOnFragment";

    private OnModeFragmentInteractionListener mListener;
    private PetimoController controller;

    private TextView textViewMonitoring;
    private TextView textViewCatTask;
    private TextView textViewDate;
    private TextView textViewStartTime;
    private Button buttonStop;

    public ModeOnFragment() {
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
            mListener = (OnModeFragmentInteractionListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnModeFragmentInteractionListener");
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
                new ConfirmStopDialogFragment().show(getFragmentManager(),null);
            }
        });

        // Update view
        textViewMonitoring.setText(
                "< " + getString(R.string.onmodefragment_text_view_monitoring) + " >");
        Log.d(TAG, "Controller is null ===> " + (controller==null));
        String[] monitorInfo = controller.getLiveMonitorInfo();
        textViewCatTask.setText(monitorInfo[0] + " / " + monitorInfo[1]);
        textViewDate.setText(
                getString(R.string.onmodefragment_text_view_date) + " " + monitorInfo[2]);
        textViewStartTime.setText(
                getString(R.string.onmodefragment_text_view_starttime) + " " + monitorInfo[3]);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
