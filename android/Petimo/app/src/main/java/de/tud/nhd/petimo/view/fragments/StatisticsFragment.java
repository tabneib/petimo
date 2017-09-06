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
public class StatisticsFragment extends Fragment {

    private static StatisticsFragment _instance;
    
    public StatisticsFragment() {
        // Required empty public constructor
    }

    /**
     * Return an (unique) instance of {@link StatisticsFragment}, if not yet exists then initialize
     * @return the StatisticsFragment instance
     */
    public static StatisticsFragment getInstance(){
        if (_instance == null)
            return new StatisticsFragment();
        else
            return _instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

}
