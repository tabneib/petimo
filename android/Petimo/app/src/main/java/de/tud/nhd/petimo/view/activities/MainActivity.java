package de.tud.nhd.petimo.view.activities;

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
import android.widget.Toast;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.controller.PetimoController;
import de.tud.nhd.petimo.controller.ResponseCode;
import de.tud.nhd.petimo.controller.exception.DbErrorException;
import de.tud.nhd.petimo.controller.exception.InvalidCategoryException;
import de.tud.nhd.petimo.controller.exception.InvalidInputNameException;
import de.tud.nhd.petimo.view.fragments.DemoFragment;
import de.tud.nhd.petimo.view.fragments.EditBlocksFragment;
import de.tud.nhd.petimo.view.fragments.EditTasksFragment;
import de.tud.nhd.petimo.view.fragments.ModeOffFragment;
import de.tud.nhd.petimo.view.fragments.ModeOnFragment;
import de.tud.nhd.petimo.view.fragments.listener.OnEditTaskFragmentInteractionListener;
import de.tud.nhd.petimo.view.fragments.listener.OnModeFragmentInteractionListener;
import de.tud.nhd.petimo.view.fragments.SettingFragment;
import de.tud.nhd.petimo.view.fragments.StatisticsFragment;
import de.tud.nhd.petimo.view.fragments.lists.MonitorCategoryListFragment;
import de.tud.nhd.petimo.view.fragments.lists.adapters.MonitorCategoryRecyclerViewAdapter;

public class MainActivity extends AppCompatActivity
        implements OnModeFragmentInteractionListener, OnEditTaskFragmentInteractionListener{

    final String TAG = "MainActivity";
    Toolbar toolBar;
    PetimoController controller;

    //ModeOffFragment modeOffFragment;
    //ModeOnFragment modeOnFragment;

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
        toolBar.setNavigationIcon(R.drawable.ic_drawer);
        setSupportActionBar(toolBar);

        title = drawerTitle = getTitle();
        titleList = getResources().getStringArray(R.array.navigation_titles);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, titleList));
        drawerList.setOnItemClickListener(new OnDrawerItemClickListener());
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.drawer_open, R.string.drawer_close){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                toolBar.setTitle(drawerTitle);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                toolBar.setTitle(title);
                invalidateOptionsMenu();
            }
        };

        // Set the drawer toggle as the DrawerListener
        drawerLayout.addDrawerListener(drawerToggle);

       // toolBar.
        //toolBar.setDisplayHomeAsUpEnabled(true);
        //toolBar.setHomeButtonEnabled(true);


        // Initialize the controller
        PetimoController.setContext(this);
        this.controller = PetimoController.getInstance();


        // Choose the displaying mode
        chooseDisplay(0);
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
                displayFragment(EditBlocksFragment.getInstance());
                break;
            case 2:
                //Statistics
                displayFragment(StatisticsFragment.getInstance());
                break;
            case 3:
                //Manage Tasks
                displayFragment(EditTasksFragment.getInstance());
                break;
            case 4:
                //Setting
                displayFragment(SettingFragment.getInstance());
                break;
            case 5:
                //Demo
                displayFragment(DemoFragment.getInstance());
                break;
            default:
                //Monitor
                chooseModeToDisplay();

        }
        // Highlight the selected item on the navigation drawer,
        // update the title
        drawerList.setItemChecked(position, true);
    }


    /**
     * Choose the monitor mode (on/off) to display
     */
    private void chooseModeToDisplay(){
        if (controller.isMonitoring())
            // There is ongoing live monitor
            displayFragment(ModeOnFragment.getInstance());
        else
            // No ongoing live monitor
            displayFragment(ModeOffFragment.getInstance());

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
    public void onConfirmAddingCatButtonClicked(
            MonitorCategoryListFragment catListFragment, String inputCat, int priority) {

        try{
            controller.addCategory(inputCat, priority);
        }
        catch (DbErrorException e){
            // TODO Notify the user !
        }
        catch (InvalidCategoryException e){
            // TODO Check for this during the user is typing !
        }
        catch(InvalidInputNameException e){
            // TODO Check for this during the user is typing !
        }

        // TODO display a snack bar to notify the usr
        // Just for now: display a Toast
        Toast.makeText(this, "Added new category: " + inputCat, Toast.LENGTH_LONG).show();

        // Update the recyclerView
        catListFragment.updateView(inputCat);


        /* Hard-coded: Re-add the whole MonitorCategoryListFragment
        getActivity().getSupportFragmentManager().beginTransaction().
                remove(MonitorCategoryListFragment.getInstance()).commit();

        getActivity().getSupportFragmentManager().beginTransaction().add(
                R.id.tasks_list_fragment_container,
                MonitorCategoryListFragment.getInstance()).commit();*/

    }

    @Override
    public void onConfirmAddingTaskStopButtonClicked(
            MonitorCategoryRecyclerViewAdapter.ViewHolder viewHolder,
            String inputCat, String inputTask, int priority) {

        // Add new task
        try{
            controller.addTask(inputTask, inputCat, priority);
        }
        catch (InvalidInputNameException e){
            e.printStackTrace();
            // TODO
        }
        catch (InvalidCategoryException e){
            e.printStackTrace();
            // TODO
        }
        catch (DbErrorException e){
            e.printStackTrace();
            // TODO
        }
        // TODO display a snack bar to notify the usr
        // Just for now: display a Toast
        Toast.makeText(this, "Added new category: " + inputCat, Toast.LENGTH_LONG).show();

        // Update the recyclerView
        viewHolder.updateView(inputTask, inputCat);

    }


    //--------------------------------------------------------------------------------------------->
    //  Drawer
    //<---------------------------------------------------------------------------------------------

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
            toolBar.setTitle(mTitle);
        }
    }

}
