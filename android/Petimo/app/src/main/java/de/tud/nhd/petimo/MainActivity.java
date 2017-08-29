package de.tud.nhd.petimo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import de.tud.nhd.petimo.model.PetimoDbDemo;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try{
            PetimoDbDemo demo = new PetimoDbDemo(this);
            while (!demo.execute()){
                // Do nothing (busy loop until execution is successful)
                System.out.println("Attempt to execute Demo: fail");
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }


    }
}
