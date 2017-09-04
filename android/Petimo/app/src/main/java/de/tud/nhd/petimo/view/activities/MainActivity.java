package de.tud.nhd.petimo.view.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.controller.PetimoController;

public class MainActivity extends AppCompatActivity {

    final String TAG = "MainActivity";
    Toolbar toolBar;
    PetimoController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolBar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolBar);

        // Initialize the controller
        try {
            PetimoController.initialize(this);
            this.controller = PetimoController.getInstance();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Choose the displaying mode
        if (controller.isMonitoring())
            displayOnMode();
        else
            displayOffMode();
    }

    /**
     * Display the OffModeActivity (no ongoing live monitor)
     */
    public void displayOffMode(){
        Intent intent = new Intent(this, OffModeActivity.class);
        startActivity(intent);
    }

    /**
     * Display the OnModeActivity (there is ongoing live monitor)
     */
    public void displayOnMode(){
        Intent intent = new Intent(MainActivity.this, OnModeActivity.class);
        MainActivity.this.startActivity(intent);
    }
}
