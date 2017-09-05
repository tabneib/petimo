package de.tud.nhd.petimo.view.activities;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.controller.PetimoController;
import de.tud.nhd.petimo.controller.ResponseCode;
import de.tud.nhd.petimo.view.fragments.ModeOnFragment;
import de.tud.nhd.petimo.view.fragments.OnModeFragmentInteractionListener;

public class OnModeActivity extends AppCompatActivity
    implements OnModeFragmentInteractionListener {

    final String TAG = "OnModeActivity";
    PetimoController controller;
    FragmentTransaction fragmentTransaction;
    Toolbar toolBar;
    private ModeOnFragment modeOnFragment = new ModeOnFragment();

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

        // Fill the view with the ModeOnFragment
        fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.activity_on_mode_fragment_container, modeOnFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onConfirmStartButtonClicked(String inputCat, String inputTask) {
        // do nothing
    }

    @Override
    public void onConfirmStopButtonClicked() {
        // Stop the monitor
        ResponseCode resCode = controller.addBlockLive(null, null);
        Log.d(TAG, resCode.toString());
        // Switch to OffModeActivity
        Intent intent = new Intent(this, MonitorResultActivity.class);
        startActivity(intent);
    }
}
