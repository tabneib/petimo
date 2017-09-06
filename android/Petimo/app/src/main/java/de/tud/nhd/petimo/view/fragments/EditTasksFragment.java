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
public class EditTasksFragment extends Fragment {

    private static EditTasksFragment _instance;
    public EditTasksFragment() {
        // Required empty public constructor
    }

    /**
     * Return an (unique) instance of {@link EditTasksFragment}, if not yet exists then initialize
     * @return the EditTaskFragment instance
     */
    public static EditTasksFragment getInstance(){
        if(_instance == null)
            return new EditTasksFragment();
        else return _instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_tasks, container, false);
    }

}