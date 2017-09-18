package de.tud.nhd.petimo.view.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.tud.nhd.petimo.libs.HorizontalPicker;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.model.PetimoSharedPref;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    public static final String TAG = "SettingsFragment";
    private static SettingsFragment _instance;

    private HorizontalPicker ovPicker;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Return an (unique) instance of {@link SettingsFragment}, if not yet exists then initialize
     * @return the SettingsFragment instance
     */
    public static SettingsFragment getInstance(){
        if (_instance == null){
            _instance = new SettingsFragment();
            return _instance;
        }
        else
            return _instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ovPicker = (HorizontalPicker) view.findViewById(R.id.horizontal_picker_ov);
        ovPicker.setSelectedItem(PetimoSharedPref.getInstance().
                getSettingsInt(PetimoSharedPref.SETTINGS_OVERNIGHT_THRESHOLD, 5));
        ovPicker.setOnItemSelectedListener(new HorizontalPicker.OnItemSelected() {
            @Override
            public void onItemSelected(int index) {
                PetimoSharedPref.getInstance().
                        setSettingsInt(PetimoSharedPref.SETTINGS_OVERNIGHT_THRESHOLD, index);
                Log.d(TAG, "chosen ====> " + index);
            }
        });
    }


}
