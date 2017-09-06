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
public class SettingFragment extends Fragment {

    private static SettingFragment _instance;
    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * Return an (unique) instance of {@link SettingFragment}, if not yet exists then initialize
     * @return the SettingFragment instance
     */
    public static SettingFragment getInstance(){
        if (_instance == null)
            return new SettingFragment();
        else
            return _instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

}
