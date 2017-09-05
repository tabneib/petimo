package de.tud.nhd.petimo.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.controller.PetimoController;
import de.tud.nhd.petimo.view.fragments.ModeOffFragment;
import de.tud.nhd.petimo.view.fragments.OnModeFragmentInteractionListener;

public class OffModeActivity extends AppCompatActivity
        implements OnModeFragmentInteractionListener {

    final String TAG = "OffModeActivity";
    PetimoController controller;
    FragmentTransaction fragmentTransaction;
    Toolbar toolBar;
    private ModeOffFragment modeOffFragment = new ModeOffFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_off_mode);

        toolBar = (Toolbar) findViewById(R.id.activity_off_mode_toolbar);
        setSupportActionBar(toolBar);

        try{
            controller = PetimoController.getInstance();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        // Fill the view with the ModeOffFragment
        fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.activity_off_mode_fragment_container, modeOffFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onConfirmStartButtonClicked(String inputCat, String inputTask) {
        // Start the monitor
        controller.addBlockLive(inputCat, inputTask);
        // Switch to OnModeActivity
        Intent intent = new Intent(this, OnModeActivity.class);
        startActivity(intent);
    }

    @Override
    public void onConfirmStopButtonClicked() {

    }
}
