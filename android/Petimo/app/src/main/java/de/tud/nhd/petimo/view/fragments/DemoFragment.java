package de.tud.nhd.petimo.view.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.model.PetimoDbDemo;

/**
 * A simple {@link Fragment} subclass.
 */
public class DemoFragment extends Fragment {

    private static DemoFragment _instance;

    private PetimoDbDemo demo;

    public DemoFragment() {
        // Required empty public constructor
    }

    /**
     * Return an (unique) instance of {@link DemoFragment}, if not yet exists then initialize
     * @return the DemoFragment instance
     */
    public static DemoFragment getInstance(){
        if (_instance == null){
            _instance = new DemoFragment();
            return _instance;
        }
        else
            return _instance;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        demo = new PetimoDbDemo(getActivity().getParent());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_demo, container, false);
    }


    @Override
    public void onStart() {
        super.onStart();
        Button demoButton = (Button) getActivity().findViewById(R.id.button_demo);
        demoButton.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                demo.executeDemo();
            }
        });
    }
}
