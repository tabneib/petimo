package de.tud.nhd.petimo.view.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.controller.PetimoController;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OffModeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class OffModeFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    Spinner catSpinner;
    Spinner taskSpinner;
    PetimoController controller;

    public OffModeFragment() {
        // Required empty public constructor
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
        try {
            this.controller = PetimoController.getInstance();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        new WaitForDb().execute((Void) null);
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     *
     */
    private void setSpinners(){
        catSpinner = (Spinner) getView().findViewById(R.id.spinnerCat);
        taskSpinner = (Spinner) getView().findViewById(R.id.spinnerTask);
        ArrayAdapter<String> catSpinnerAdapter = new ArrayAdapter<String>(this.getContext(),
                R.layout.support_simple_spinner_dropdown_item, controller.getAllCatNames());
        ArrayAdapter<String> taskSpinnerAdapter = new ArrayAdapter<String>(this.getContext(),
                R.layout.support_simple_spinner_dropdown_item, controller.getAllTaskNames());
        catSpinner.setAdapter(catSpinnerAdapter);
        taskSpinner.setAdapter(taskSpinnerAdapter);
        catSpinnerAdapter.notifyDataSetChanged();
        taskSpinnerAdapter.notifyDataSetChanged();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class WaitForDb extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... params) {
            while (!controller.isDbReady()){
                try{
                    Thread.sleep(20);
                    System.out.println("is db ready?");
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
