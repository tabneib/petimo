package de.tud.nhd.petimo.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.controller.PetimoController;

public class MainActivity extends AppCompatActivity {

    public final String TAG = "MainActivity";
    private Button buttonDemo;
    private PetimoController controller;


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

        if (controller.isMonitoring())
            displayOnMode();
        else
            displayOffMode();
        // Demo
        /*final PetimoDbDemo demo = new PetimoDbDemo(this);
        buttonDemo = (Button) findViewById(R.id.button_exec_demo);
        buttonDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                demo.execute();
            }
        });*/


    }

    /**
     * Display the off mode (no ongoing live monitor)
     */
    public void displayOffMode(){


    }

    /**
     * Display the on mode (there is ongoing live monitor)
     */
    public void displayOnMode(){

    }

}
