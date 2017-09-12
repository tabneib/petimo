package de.tud.nhd.petimo.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.view.fragments.listener.OnModeFragmentInteractionListener;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnModeFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ModeOffMenuFragment extends Fragment {

    private OnModeFragmentInteractionListener mListener;

    public ModeOffMenuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_off_mode_menu, container, false);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnModeFragmentInteractionListener) {
            mListener = (OnModeFragmentInteractionListener) context;
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

}
