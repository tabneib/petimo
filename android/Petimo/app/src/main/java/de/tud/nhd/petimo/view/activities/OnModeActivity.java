package de.tud.nhd.petimo.view.activities;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.controller.PetimoController;
import de.tud.nhd.petimo.view.fragments.OnMainActivityFragmentInteractionListener;
import de.tud.nhd.petimo.view.fragments.OnModeFragment;

public class OnModeActivity extends AppCompatActivity
    implements OnMainActivityFragmentInteractionListener {

    final String TAG = "OnModeActivity";
    PetimoController controller;
    FragmentTransaction fragmentTransaction;
    Toolbar toolBar;
    private OnModeFragment onModeFragment = new OnModeFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_mode);
        toolBar = (Toolbar) findViewById(R.id.activity_on_mode_toolbar);
        setSupportActionBar(toolBar);
        try{
        controller = PetimoController.getInstance();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        // Fill the view with the OnModeFragment
        fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.activity_on_mode_fragment_container, onModeFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onConfirmStartButtonClicked(String inputCat, String inputTask) {
        // do nothing
    }

    @Override
    public void onConfirmStopButtonClicked() {
        // Stop the monitor
        controller.addBlockLive(null, null);
        // Switch to OffModeActivity
        Intent intent = new Intent(this, OffModeActivity.class);
        startActivity(intent);
    }
}
