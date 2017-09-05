package de.tud.nhd.petimo.view.activities;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.controller.PetimoController;
import de.tud.nhd.petimo.controller.ResponseCode;
import de.tud.nhd.petimo.controller.exception.DbErrorException;
import de.tud.nhd.petimo.controller.exception.InvalidCategoryException;
import de.tud.nhd.petimo.view.fragments.ModeOffFragment;
import de.tud.nhd.petimo.view.fragments.ModeOnFragment;
import de.tud.nhd.petimo.view.fragments.OnModeFragmentInteractionListener;

public class MainActivity extends AppCompatActivity implements OnModeFragmentInteractionListener{

    final String TAG = "MainActivity";
    Toolbar toolBar;
    PetimoController controller;

    ModeOffFragment modeOffFragment;
    ModeOnFragment modeOnFragment;

    private CharSequence drawerTitle;
    private CharSequence title;
    private String[] titleList;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set the action bar
        toolBar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolBar);

        modeOffFragment = new ModeOffFragment();
        modeOnFragment = new ModeOnFragment();

        title = drawerTitle = getTitle();
        titleList = getResources().getStringArray(R.array.navigation_titles);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        Log.d(TAG, "drawerLayout is null ===> " + (drawerLayout == null));
        drawerList = (ListView) findViewById(R.id.left_drawer);
        Log.d(TAG, "drawerList is null ===> " + (drawerList == null));
        drawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, titleList));
        drawerList.setOnItemClickListener(new OnDrawerItemClickListener());
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.drawer_open, R.string.drawer_close){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle(drawerTitle);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActionBar().setTitle(title);
                invalidateOptionsMenu();
            }
        };

        // Set the drawer toggle as the DrawerListener
        drawerLayout.addDrawerListener(drawerToggle);

        //toolBar.setDisplayHomeAsUpEnabled(true);
        //toolBar.setHomeButtonEnabled(true);


        // Initialize the controller
        try {
            PetimoController.initialize(this);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        try{
            this.controller = PetimoController.getInstance();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Choose the displaying mode
        chooseDisplay(0);
    }



    /**
     *
     * @param position
     */
    private void chooseDisplay(int position) {
        // Create a new fragment and specify the planet to show based on position
        switch (position) {
            case 1:
                //Monitored Tasks
                 // TODO remove me
                //TODO
                break;
            case 2:
                //Statistics
                displayFragment(modeOffFragment); // TODO remove me
                //TODO
                break;
            case 3:
                //Manage Tasks
                displayFragment(modeOffFragment); // TODO remove me
                //TODO
                break;
            case 4:
                //Setting
                displayFragment(modeOffFragment); // TODO remove me
                //TODO
                break;
            case 5:
                //Demo
                displayFragment(modeOffFragment); // TODO remove me
                //TODO
                break;
            default:
                //Monitor
                Log.d(TAG, " Monitoring?  ===> " + controller.isMonitoring());
                if (controller.isMonitoring())
                    // Display the ModeOnFragment (there is ongoing live monitor)
                    displayFragment(modeOnFragment);
                else
                    // Display the ModeOffFragment (no ongoing live monitor)
                    displayFragment(modeOffFragment);
        }
        // Highlight the selected item on the navigation drawer,
        // update the title
        drawerList.setItemChecked(position, true);
    }

    /**
     *
     * @param fragment
     */
    private void displayFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().
                replace(R.id.content_frame, fragment).commit();
    }

    @Override
    public void onConfirmStartButtonClicked(String inputCat, String inputTask) {
        // Start the monitor
        try {
            controller.addBlockLive(inputCat, inputTask);
        } catch (InvalidCategoryException e){
            // TODO
        } catch (DbErrorException e){
            // TODO
        }
        // Switch the fragment (to OnModeFragment)
        chooseDisplay(0);
    }

    @Override
    public void onConfirmStopButtonClicked() {
        // Stop the monitor
        ResponseCode resCode = null;
        try {
            resCode = controller.addBlockLive(null, null);
        } catch (DbErrorException e) {
            // TODO
        } catch (InvalidCategoryException e) {
            // TODO
        }
        Log.d(TAG, resCode.toString());
        // Switch to OffModeActivity
        Intent intent = new Intent(this, MonitorResultActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        /*boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);*/
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...
        return super.onOptionsItemSelected(item);
    }


    /**
     *
     */
    private class OnDrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            chooseDisplay(position);
            // Close the drawer
            drawerLayout.closeDrawer(drawerList);
        }

        public void setTitle(CharSequence title) {
            CharSequence mTitle = title;
            getActionBar().setTitle(mTitle);
        }
    }

}
