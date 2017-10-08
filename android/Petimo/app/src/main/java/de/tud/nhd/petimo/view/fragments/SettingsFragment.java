package de.tud.nhd.petimo.view.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.security.MessageDigest;

import de.tud.nhd.petimo.controller.PetimoController;
import de.tud.nhd.petimo.libs.HorizontalPicker;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.model.db.PetimoDbDemo;
import de.tud.nhd.petimo.model.sharedpref.SharedPref;
import de.tud.nhd.petimo.view.fragments.dialogs.PetimoDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    public static final String TAG = "SettingsFragment";
    private static SettingsFragment _instance;
    private PetimoDbDemo demo;
    // password to executeDemo demo
    private final String md5Pwd = "6b3e58be7169f200c66594f235c0a665";

    private HorizontalPicker ovPicker;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        demo = new PetimoDbDemo(getActivity().getParent());

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

        // Language
        Spinner langSpinner = (Spinner) view.findViewById(R.id.spinnerLang);
        langSpinner.setSelection(
                PetimoController.getInstance().getLangId(
                        SharedPref.getInstance().getSettingsString(
                                SharedPref.SETTINGS_LANGUAGE, SharedPref.LANG_EN)));
        langSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPref.getInstance().setSettingsString(
                        SharedPref.SETTINGS_LANGUAGE,
                        PetimoController.getInstance().getLangFromId(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Demo
        Button demoButton = (Button) getActivity().findViewById(R.id.button_demo);
        demoButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PetimoDialog demoDialog = PetimoDialog.newInstance(getActivity())
                                .setIcon(PetimoDialog.ICON_WARNING)
                                .setTitle("Enter Password")
                                .setContentLayout(R.layout.dialog_demo)
                                .setPositiveButton("Execute Demo",
                                        new PetimoDialog.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                try {
                                                    MessageDigest md = MessageDigest.getInstance("MD5");
                                                    EditText pwd =
                                                            (EditText) view.findViewById(R.id.pwd);
                                                    md.update(pwd.getText().toString().getBytes());
                                                    byte[] digest = md.digest();
                                                    StringBuffer sb = new StringBuffer();
                                                    for (byte b : digest) {
                                                        sb.append(String.format("%02x", b & 0xff));
                                                    }
                                                    Log.d(TAG, "digest ==> " + sb.toString());


                                                    if (sb.toString().equals(md5Pwd)) {
                                                        demo.executeDemo();
                                                        Toast.makeText(getActivity(),
                                                                "Demo executed!",
                                                                Toast.LENGTH_LONG).show();
                                                        return;
                                                    }
                                                    else
                                                        Toast.makeText(getActivity(),
                                                                "Wrong Password",
                                                                Toast.LENGTH_LONG).show();
                                                }
                                                catch (Exception e){
                                                    e.getMessage();
                                                }

                                            }
                                        });
                        demoDialog.show(getActivity().getSupportFragmentManager(), null);
                    }
                });


        ovPicker = (HorizontalPicker) view.findViewById(R.id.horizontal_picker_ov);
        ovPicker.setSelectedItem(SharedPref.getInstance().
                getSettingsInt(SharedPref.SETTINGS_OVERNIGHT_THRESHOLD, 5));
        ovPicker.setOnItemSelectedListener(new HorizontalPicker.OnItemSelected() {
            @Override
            public void onItemSelected(int index) {
                SharedPref.getInstance().
                        setSettingsInt(SharedPref.SETTINGS_OVERNIGHT_THRESHOLD, index);
            }
        });
    }


}
