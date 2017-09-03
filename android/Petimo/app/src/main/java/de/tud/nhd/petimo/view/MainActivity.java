package de.tud.nhd.petimo.view;

import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.controller.PetimoController;
import de.tud.nhd.petimo.view.asynctasks.WaitForDb;
import de.tud.nhd.petimo.view.fragments.OffModeFragment;
import de.tud.nhd.petimo.view.fragments.OnModeFragment;

public class MainActivity extends AppCompatActivity implements
        OffModeFragment.OnFragmentInteractionListener {

    final String TAG = "MainActivity";
    PetimoController controller;
    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    private OffModeFragment offModeFragment = new OffModeFragment();
    private OnModeFragment onModeFragment = new OnModeFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolBar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolBar);

        try {
            PetimoController.initialize(this);
            this.controller = PetimoController.getInstance();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        // Choose the displaying mode on start up
        if (controller.isMonitoring())
            displayOnMode(true);
        else
            displayOffMode(true);
    }


    /**
     * Display the off mode (no ongoing live monitor)
     */
    public void displayOffMode(boolean isOnCreate){
        if (isOnCreate){
            fragmentTransaction.add(R.id.activity_main, offModeFragment);
            fragmentTransaction.commit();
            new WaitForDb(offModeFragment, controller).execute((Void) null);
        }
        else{
            fragmentTransaction.remove(onModeFragment);
            fragmentTransaction.add(R.id.activity_main, offModeFragment);
            fragmentTransaction.commit();
        }
    }

    /**
     * Display the on mode (there is ongoing live monitor)
     */
    public void displayOnMode(boolean isOnCreate){
        if (isOnCreate){
            fragmentTransaction.add(R.id.activity_main, onModeFragment);
            fragmentTransaction.commit();
        }
        else{
            fragmentTransaction.remove(offModeFragment);
            fragmentTransaction.add(R.id.activity_main, onModeFragment);
            fragmentTransaction.commit();
        }
    }


    @Override
    public void onFragmentInteraction(Uri uri) {
        //do nothing
    }


    private void log(String msg){
        Log.d(TAG, msg);
   }
}
