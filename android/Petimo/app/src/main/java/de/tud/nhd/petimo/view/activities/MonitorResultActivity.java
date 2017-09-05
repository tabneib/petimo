package de.tud.nhd.petimo.view.activities;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.controller.TimeUtils;
import de.tud.nhd.petimo.model.MonitorBlock;
import de.tud.nhd.petimo.view.fragments.lists.MonitorBlockListFragment;

// TODO implement me
public class MonitorResultActivity extends AppCompatActivity
        implements MonitorBlockListFragment.OnListFragmentInteractionListener{

    private final String TAG = "MonitorResultActivity";

    Button okButton;
    MonitorBlockListFragment blockListFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_result);

        okButton = (Button) findViewById(R.id.button_ok);
        okButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MonitorResultActivity.this, OffModeActivity.class);
                MonitorResultActivity.this.startActivity(intent);
            }
        });
        blockListFragment = MonitorBlockListFragment.newInstance(
                1, TimeUtils.getTodayDate()-1, TimeUtils.getTodayDate());
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.activity_monitor_result_fragment_container, blockListFragment);
        fragmentTransaction.commit();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onListFragmentInteraction(MonitorBlock item) {

    }
}
