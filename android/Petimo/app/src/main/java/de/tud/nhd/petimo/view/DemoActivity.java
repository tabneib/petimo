package de.tud.nhd.petimo.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.model.PetimoDbDemo;

public class DemoActivity extends AppCompatActivity {

    Button buttonDemo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        // Demo
        final PetimoDbDemo demo = new PetimoDbDemo(this);
        buttonDemo = (Button) findViewById(R.id.button_exec_demo);
        buttonDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                demo.execute();
            }
        });

    }
}
