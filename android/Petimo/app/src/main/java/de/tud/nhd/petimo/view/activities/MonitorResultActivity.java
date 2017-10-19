package de.tud.nhd.petimo.view.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.utils.PetimoTimeUtils;
import de.tud.nhd.petimo.view.fragments.lists.BlockListFragment;

// TODO implement me
public class MonitorResultActivity extends AppCompatActivity {

    private final String TAG = "MonitorResultActivity";

    Button okButton;
    BlockListFragment blockListFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_result);

        okButton = (Button) findViewById(R.id.button_ok);
        okButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        blockListFragment = BlockListFragment.newInstance(
                1, PetimoTimeUtils.getTodayDate(), PetimoTimeUtils.getTodayDate());
        getSupportFragmentManager().beginTransaction().add(
                R.id.activity_monitor_result_fragment_container, blockListFragment).commit();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
