package de.tud.nhd.petimo.view.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.tud.nhd.petimo.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditBlocksFragment extends Fragment {

    private static EditBlocksFragment _instance;

    public EditBlocksFragment() {
        // Required empty public constructor
    }

    /**
     * Return an (unique) instance of {@link EditBlocksFragment}, if not yet exists then initialize
     * @return the EditBlocksFragment instance
     */
    public static EditBlocksFragment getInstance(){
        if (_instance == null){
            _instance = new EditBlocksFragment();
            return _instance;
        }
        else
            return _instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_blocks, container, false);
    }

}
