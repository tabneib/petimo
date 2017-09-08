package de.tud.nhd.petimo.view.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.view.fragments.lists.DayListFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditBlocksFragment extends Fragment {

    private static final String TAG = "EditBlocksFragment";
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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "Gonna add DayListFragment to its container !!");
        getActivity().getSupportFragmentManager().beginTransaction().add(
                R.id.day_list_fragment_container, DayListFragment.newInstance()).commit();
    }
}
