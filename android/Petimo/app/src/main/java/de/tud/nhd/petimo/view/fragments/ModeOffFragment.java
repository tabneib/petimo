package de.tud.nhd.petimo.view.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.controller.PetimoController;
import de.tud.nhd.petimo.view.fragments.dialogs.ConfirmStartDialogFragment;

/**
 * Activities that contain this fragment must implement the
 * {@link OnModeFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ModeOffFragment extends Fragment {

    private final String TAG = "ModeOffFragment";
    private OnModeFragmentInteractionListener mListener;
    Spinner catSpinner;
    Spinner taskSpinner;
    Button startButton;
    PetimoController controller;


    public ModeOffFragment() {
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

        catSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateTaskSpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    /**
     * Initialize the contents of the category spinner with data from the database
     */
    private void initCatSpinner(){
        getActivity().runOnUiThread(new Runnable(){
            @Override
            public void run(){
                ArrayAdapter<String> catSpinnerAdapter = new ArrayAdapter<String>(getContext(),
                        R.layout.support_simple_spinner_dropdown_item, controller.getAllCatNames());

                catSpinner.setAdapter(catSpinnerAdapter);
                catSpinnerAdapter.notifyDataSetChanged();

            }
        });
    }

    /**
     * Update the contents of the task spinner according to the selected item of the cat spinner
     */
    private void updateTaskSpinner(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ArrayAdapter<String> taskSpinnerAdapter = new ArrayAdapter<String>(getContext(),
                        R.layout.support_simple_spinner_dropdown_item,
                        controller.getTaskNameByCat(catSpinner.getSelectedItem().toString()));
                taskSpinner.setAdapter(taskSpinnerAdapter);
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
            initCatSpinner();
            return null;
        }
    }
}
