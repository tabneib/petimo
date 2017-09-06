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
public class DemoFragment extends Fragment {

    private static DemoFragment _instance;

    public DemoFragment() {
        // Required empty public constructor
    }

    /**
     * Return an (unique) instance of {@link DemoFragment}, if not yet exists then initialize
     * @return the DemoFragment instance
     */
    public static DemoFragment getInstance(){
        if (_instance == null)
            return new DemoFragment();
        else
            return _instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_demo, container, false);
    }

}
