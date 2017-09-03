package de.tud.nhd.petimo.view.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.controller.PetimoController;
import de.tud.nhd.petimo.view.fragments.dialogs.ConfirmStartDialogFragment;

/**
 * Activities that contain this fragment must implement the
 * {@link OnMainActivityFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class OffModeFragment extends Fragment {

    private final String TAG = "OffModeFragment";
    private OnMainActivityFragmentInteractionListener mListener;
    Spinner catSpinner;
    Spinner taskSpinner;
    Button startButton;
    PetimoController controller;


    public OffModeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.controller = PetimoController.getInstance();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        try {
            mListener = (OnMainActivityFragmentInteractionListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnMainActivityFragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_off_mode, container, false);
    }



    @Override
    public void onStart() {
        super.onStart();
        catSpinner = (Spinner) getView().findViewById(R.id.spinnerCat);
        taskSpinner = (Spinner) getView().findViewById(R.id.spinnerTask);
        startButton = (Button) getView().findViewById(R.id.buttonStart);

    }

    @Override
    public void onResume() {
        super.onResume();
        new WaitForDb().execute((Void) null);
        startButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                DialogFragment dialogFragment = new ConfirmStartDialogFragment();
                Bundle args = new Bundle();
                args.putString(ConfirmStartDialogFragment.CATEGORY,
                        catSpinner.getSelectedItem().toString());
                args.putString(ConfirmStartDialogFragment.TASK,
                        taskSpinner.getSelectedItem().toString());
                dialogFragment.setArguments(args);
                dialogFragment.show(getFragmentManager(), null);
            }
        });

    }

    /**
     * Update the contents of the spinners with data from the database
     */
    private void setSpinners(){
        getActivity().runOnUiThread(new Runnable(){
            @Override
            public void run(){
                ArrayAdapter<String> catSpinnerAdapter = new ArrayAdapter<String>(getContext(),
                        R.layout.support_simple_spinner_dropdown_item, controller.getAllCatNames());
                ArrayAdapter<String> taskSpinnerAdapter = new ArrayAdapter<String>(getContext(),
                        R.layout.support_simple_spinner_dropdown_item, controller.getAllTaskNames());
                catSpinner.setAdapter(catSpinnerAdapter);
                taskSpinner.setAdapter(taskSpinnerAdapter);
                catSpinnerAdapter.notifyDataSetChanged();
                taskSpinnerAdapter.notifyDataSetChanged();
            }
        });
    }


    /**
     * Busy loop until the db wrapper is ready. This is only done when the app is starting up
     */
    private class WaitForDb extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            while (!controller.isDbReady()){
                try{
                    Thread.sleep(20);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            setSpinners();
            return null;
        }
    }
}
