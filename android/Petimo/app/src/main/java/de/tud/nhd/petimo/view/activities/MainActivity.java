package de.tud.nhd.petimo.view.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import de.tud.nhd.petimo.controller.exception.DbErrorException;
import de.tud.nhd.petimo.controller.exception.InvalidCategoryException;
import de.tud.nhd.petimo.controller.exception.InvalidInputNameException;
import de.tud.nhd.petimo.model.MonitorBlock;
import de.tud.nhd.petimo.view.fragments.DemoFragment;
import de.tud.nhd.petimo.view.fragments.EditBlocksFragment;
import de.tud.nhd.petimo.view.fragments.EditTasksFragment;
import de.tud.nhd.petimo.view.fragments.ModeOffFragment;
import de.tud.nhd.petimo.view.fragments.ModeOnFragment;
import de.tud.nhd.petimo.view.fragments.SettingFragment;
import de.tud.nhd.petimo.view.fragments.StatisticsFragment;
import de.tud.nhd.petimo.view.fragments.listener.OnEditBlocksMenuFragmentInteractionListener;
import de.tud.nhd.petimo.view.fragments.listener.OnEditDayFragmentInteractionListener;
import de.tud.nhd.petimo.view.fragments.listener.OnEditTaskFragmentInteractionListener;
import de.tud.nhd.petimo.view.fragments.listener.OnModeFragmentInteractionListener;
import de.tud.nhd.petimo.view.fragments.lists.CategoryListFragment;
import de.tud.nhd.petimo.view.fragments.lists.adapters.CategoryRecyclerViewAdapter;

public class MainActivity extends AppCompatActivity
        implements OnModeFragmentInteractionListener, OnEditTaskFragmentInteractionListener,
        OnEditDayFragmentInteractionListener, OnEditBlocksMenuFragmentInteractionListener {

    private static final String TAG = "MainActivity";

    public static final String EDIT_BLOCKS_FRAGMENT_TAG = TAG + "VIEW_BLOCKS_FRAGMENT_TAG";
    public static final String EDIT_TASKS_FRAGMENT_TAG = TAG + "EDIT_TASKS_FRAGMENT_TAG";
    public static final String STATISTICS_FRAGMENT_TAG = TAG + "STATISTICS_FRAGMENT_TAG";
    public static final String SETTING_FRAGMENT_TAG = TAG + "SETTING_FRAGMENT_TAG";
    public static final String DEMO_FRAGMENT_TAG = TAG + "DEMO_FRAGMENT_TAG";
    public static final String MODE_OFF_FRAGMENT_TAG = TAG + "MODE_OFF_FRAGMENT_TAG";
    public static final String MODE_ON_FRAGMENT_TAG = TAG + "MODE_ON_FRAGMENT_TAG";
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

        Log.d(TAG, "Changing display to ====> " + position);
        // If the database wrapper is not yet ready
        if (!controller.isDbReady()) {
            new WaitForDb(position).execute((Void) null);
            return;
        }

        // Create a new fragment and specify the planet to show based on position
        switch (position) {
            case 1:
                //Monitored Tasks
                displayFragment(EDIT_BLOCKS_FRAGMENT_TAG);
                break;
            case 2:
                //Statistics
                displayFragment(STATISTICS_FRAGMENT_TAG);
                break;
            case 3:
                //Manage Tasks
                displayFragment(EDIT_TASKS_FRAGMENT_TAG);
                break;
            case 4:
                //Setting
                displayFragment(SETTING_FRAGMENT_TAG);
                break;
            case 5:
                //Demo
                displayFragment(DEMO_FRAGMENT_TAG);
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
            displayFragment(MODE_ON_FRAGMENT_TAG);
        else
            // No ongoing live monitor
            displayFragment(MODE_OFF_FRAGMENT_TAG);

    }


    /**
     *
     * @param fTag
     */
    private void displayFragment(String fTag){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = fm.findFragmentByTag(fTag);

        if (fragment != null)
            ft.replace(R.id.content_frame, fragment).commit();
        else{
            switch (fTag) {
                case EDIT_BLOCKS_FRAGMENT_TAG:
                    fragment = EditBlocksFragment.getInstance();
                    break;
                case EDIT_TASKS_FRAGMENT_TAG:
                    fragment = EditTasksFragment.getInstance();
                    break;
                case STATISTICS_FRAGMENT_TAG:
                    fragment = StatisticsFragment.getInstance();
                    break;
                case SETTING_FRAGMENT_TAG:
                    fragment = SettingFragment.getInstance();
                    break;
                case DEMO_FRAGMENT_TAG:
                    fragment = DemoFragment.getInstance();
                    break;
                case MODE_OFF_FRAGMENT_TAG:
                    fragment = ModeOffFragment.getInstance();
                    break;
                case MODE_ON_FRAGMENT_TAG:
                    fragment = ModeOnFragment.getInstance();
                    break;
            }
            ft.replace(R.id.content_frame, fragment, fTag).commit();
        }
    }

    //--------------------------------------------------------------------------------------------->
    // Handle Callback Listeners
    //<---------------------------------------------------------------------------------------------


    @Override
    public void onConfirmStartButtonClicked(String inputCat, String inputTask, long startTime) {
        // Start the monitor
        try {
            controller.monitor(inputCat, inputTask, startTime, 0);
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
        try {
            // update the monitored task list
            controller.updateMonitoredTaskList();
            // store the last monitored cat/task
            controller.updateLastMonitored();
            // add the monitored block
            controller.monitor(null, null, 0, System.currentTimeMillis());
        } catch (DbErrorException e) {
            // TODO
        } catch (InvalidCategoryException e) {
            // TODO
        }
        // Switch to OffModeActivity
        Intent intent = new Intent(this, MonitorResultActivity.class);
        startActivity(intent);
    }

    @Override
    public void onLastMonitoredTaskSelected(String category, String task) {
        ModeOffFragment modeOffFragment = (ModeOffFragment)
                getSupportFragmentManager().findFragmentByTag(MODE_OFF_FRAGMENT_TAG);
        if (modeOffFragment != null){
            modeOffFragment.updateAllSpinner(category, task);
            controller.updateLastMonitored(category, task);
        }
    }

    @Override
    public void onConfirmAddingCatButtonClicked(
            CategoryListFragment catListFragment, String inputCat, int priority) {

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


        /* Hard-coded: Re-add the whole CategoryListFragment
        getActivity().getSupportFragmentManager().beginTransaction().
                remove(CategoryListFragment.getInstance()).commit();

        getActivity().getSupportFragmentManager().beginTransaction().add(
                R.id.tasks_list_fragment_container,
                CategoryListFragment.getInstance()).commit();*/

    }

    @Override
    public void onConfirmAddingTaskStopButtonClicked(
            CategoryRecyclerViewAdapter.ViewHolder viewHolder,
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


    @Override
    public void onRemovingMonitorBlock(MonitorBlock item) {

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
        // Handle other action bar items..

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

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

    /**
     * Busy loop until the db wrapper is ready. This is only done when the app is starting up
     */
    private class WaitForDb extends AsyncTask<Void, Void, Void> {

        private int position;
        public WaitForDb(int position){
            this.position = position;
        }
        @Override
        protected Void doInBackground(Void... params) {
            while (!controller.isDbReady()){
                try{
                    Log.d(TAG, "waiting for DB");
                    Thread.sleep(20);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            chooseDisplay(position);
            return null;
        }
    }
}
