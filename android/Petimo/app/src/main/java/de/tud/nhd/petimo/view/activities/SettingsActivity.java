package de.tud.nhd.petimo.view.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.security.MessageDigest;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.controller.PetimoController;
import de.tud.nhd.petimo.libs.HorizontalPicker;
import de.tud.nhd.petimo.model.db.PetimoDbDemo;
import de.tud.nhd.petimo.model.sharedpref.PetimoSettingsSPref;
import de.tud.nhd.petimo.model.sharedpref.SharedPref;
import de.tud.nhd.petimo.view.fragments.dialogs.PetimoDialog;

public class SettingsActivity extends AppCompatActivity {


    public static final String TAG = "SettingsActivity";
    private PetimoDbDemo demo;
    // Password to executeDemo demo
    private final String md5Pwd = "6b3e58be7169f200c66594f235c0a665";

    private HorizontalPicker ovPicker;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Toolbar
        mToolbar = (Toolbar) findViewById(R.id.activity_settings_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Language
        Spinner langSpinner = (Spinner) findViewById(R.id.spinnerLang);
        langSpinner.setSelection(
                PetimoController.getInstance().getLangId(
                        PetimoSettingsSPref.getInstance().getString(
                                PetimoSettingsSPref.LANGUAGE, PetimoSettingsSPref.LANG_EN)));
        langSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                PetimoSettingsSPref.getInstance().putString(
                        PetimoSettingsSPref.LANGUAGE,
                        PetimoController.getInstance().getLangFromId(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Demo
        Button demoButton = (Button) findViewById(R.id.button_demo);
        demoButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PetimoDialog demoDialog = PetimoDialog.newInstance(getBaseContext())
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

                                                    if (sb.toString().equals(md5Pwd)) {
                                                        demo.executeDemo();
                                                        Toast.makeText(getBaseContext(),
                                                                "Demo executed!",
                                                                Toast.LENGTH_LONG).show();
                                                        return;
                                                    }
                                                    else
                                                        Toast.makeText(getBaseContext(),
                                                                "Wrong Password",
                                                                Toast.LENGTH_LONG).show();
                                                }
                                                catch (Exception e){
                                                    e.getMessage();
                                                }

                                            }
                                        });
                        demoDialog.show(getSupportFragmentManager(), null);
                    }
                });


        ovPicker = (HorizontalPicker) findViewById(R.id.horizontal_picker_ov);
        ovPicker.setSelectedItem(PetimoSettingsSPref.getInstance().
                getInt(PetimoSettingsSPref.OVERNIGHT_THRESHOLD, 5));
        ovPicker.setOnItemSelectedListener(new HorizontalPicker.OnItemSelected() {
            @Override
            public void onItemSelected(int index) {
                PetimoSettingsSPref.getInstance().
                        putInt(PetimoSettingsSPref.OVERNIGHT_THRESHOLD, index);
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }
}
