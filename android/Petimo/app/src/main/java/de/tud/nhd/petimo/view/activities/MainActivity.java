package de.tud.nhd.petimo.view.activities;

import android.content.Context;
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
import de.tud.nhd.petimo.model.db.PetimoDbWrapper;
import de.tud.nhd.petimo.model.sharedpref.SharedPref;
import de.tud.nhd.petimo.utils.PetimoContextWrapper;
import de.tud.nhd.petimo.view.fragments.DemoFragment;
import de.tud.nhd.petimo.view.fragments.EditTasksFragment;
import de.tud.nhd.petimo.view.fragments.ModeOffFragment;
import de.tud.nhd.petimo.view.fragments.ModeOnFragment;
import de.tud.nhd.petimo.view.fragments.SettingsFragment;
import de.tud.nhd.petimo.view.fragments.listener.OnEditBlocksMenuFragmentInteractionListener;
import de.tud.nhd.petimo.view.fragments.listener.OnEditTaskFragmentInteractionListener;
import de.tud.nhd.petimo.view.fragments.listener.OnModeFragmentInteractionListener;
import de.tud.nhd.petimo.view.fragments.lists.CategoryListFragment;
import de.tud.nhd.petimo.view.fragments.lists.adapters.CategoryRecyclerViewAdapter;

public class MainActivity extends AppCompatActivity
        implements OnModeFragmentInteractionListener, OnEditTaskFragmentInteractionListener,
        OnEditBlocksMenuFragmentInteractionListener {

    private static final String TAG = "PetimoMainActivity";

    public static final String EDIT_BLOCKS_FRAGMENT_TAG = TAG + "VIEW_BLOCKS_FRAGMENT_TAG";
    public static final String EDIT_TASKS_FRAGMENT_TAG = TAG + "EDIT_TASKS_FRAGMENT_TAG";
    public static final String STATISTICS_FRAGMENT_TAG = TAG + "STATISTICS_FRAGMENT_TAG";
    public static final String SETTING_FRAGMENT_TAG = TAG + "SETTING_FRAGMENT_TAG";
    public static final String DEMO_FRAGMENT_TAG = TAG + "DEMO_FRAGMENT_TAG";
    public static final String MODE_OFF_FRAGMENT_TAG = TAG + "MODE_OFF_FRAGMENT_TAG";
    public static final String MODE_ON_FRAGMENT_TAG = TAG + "MODE_ON_FRAGMENT_TAG";
    Toolbar toolBar;
    PetimoController controller;

    /**
     * static field to store the currently displayed fragment
     */
    public static int displayFragment = 0;

    //ModeOffFragment modeOffFragment;
    //ModeOnFragment modeOnFragment;

    private CharSequence drawerTitle;
    private CharSequence title;
    private String[] titleList;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;


    @Override
    protected void attachBaseContext(Context newBase) {
        PetimoController.setContext(newBase);
        this.controller = PetimoController.getInstance();
        // Update Language
        super.attachBaseContext(PetimoContextWrapper.wrapLanguage(newBase, SharedPref.getInstance().
                getSettingsString(SharedPref.SETTINGS_LANGUAGE, SharedPref.LANG_EN)));
    }


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

        //-------------------- Navigation Drawer -------------------------------------------------->

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, titleList));
        drawerList.setOnItemClickListener(new OnDrawerItemClickListener());

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.drawer_open, R.string.drawer_close){


            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //toolBar.setTitle(drawerTitle);

                supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                //toolBar.setTitle(title);

                supportInvalidateOptionsMenu();
            }
        };

        // Set the drawer toggle as the DrawerListener
        drawerLayout.addDrawerListener(drawerToggle);


        // Choose the displaying mode
        chooseDisplay(displayFragment);

        // debug
        //Log.d(TAG, "gonna run the db demo!");
        //new PetimoDbDemo(this).executeDemo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // TODO Hard-coded, no item should be selected !
        drawerList.setItemChecked(0, true);
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


    /**
     *
     * @param position
     */
    private void chooseDisplay(int position) {

        //Log.d(TAG, "Changing display to " + position);
        // If the database wrapper is not yet ready
        if (!PetimoDbWrapper.getInstance().isReady()) {
            new WaitForDb(position).execute((Void) null);
            return;
        }

        // Create a new fragment and specify the planet to show based on position
        switch (position) {
            case 1:
                //Monitored Tasks
                displayFragment = 1;
                displayFragment(EDIT_BLOCKS_FRAGMENT_TAG);
                break;
            case 2:
                //Statistics
                // TODO: step by step change all display to activity instead of fragment
                //displayFragment = 2;
                displayFragment(STATISTICS_FRAGMENT_TAG);
                break;
            case 3:
                //Manage Tasks
                displayFragment = 3;
                displayFragment(EDIT_TASKS_FRAGMENT_TAG);
                break;
            case 4:
                //Setting
                displayFragment = 4;
                displayFragment(SETTING_FRAGMENT_TAG);
                break;
            case 5:
                //Demo
                displayFragment = 5;
                displayFragment(DEMO_FRAGMENT_TAG);
                break;
            default:
                //Monitor
                displayFragment = 0;
                setTitle(titleList[0]);
                chooseModeToDisplay();

        }
        // Highlight the selected item on the navigation drawer,
        // update the title
        drawerList.setItemChecked(position, true);
        //Log.d(TAG, "Changed display to " + position);
    }

    /**
     * Choose the monitor mode (on/off) to display
     */
    private void chooseModeToDisplay(){
        if (SharedPref.getInstance().isMonitoring())
            // There is ongoing live monitor
            displayFragment(MODE_ON_FRAGMENT_TAG);
        else
            // No ongoing live monitor
            displayFragment(MODE_OFF_FRAGMENT_TAG);
    }

    /**
     * TODO: step by step change to use activity instead of fragment !
     * @param fTag
     */
    private void displayFragment(String fTag){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = fm.findFragmentByTag(fTag);

        if (fragment != null)
            ft.replace(R.id.content_frame, fragment).commit();
        else{
            Intent intent;
            switch (fTag) {
                case EDIT_BLOCKS_FRAGMENT_TAG:
                    /*fragment = EditBlocksFragment.getInstance();
                    break;*/
                    intent = new Intent(this, EditBlockActivity.class);
                    startActivity(intent);
                    return;
                case EDIT_TASKS_FRAGMENT_TAG:
                    fragment = EditTasksFragment.getInstance();
                    break;
                case STATISTICS_FRAGMENT_TAG:
                    /*fragment = StatisticsFragment.getInstance();
                    break;*/
                    intent = new Intent(this, StatisticsActivity.class);
                    startActivity(intent);
                    return;
                case SETTING_FRAGMENT_TAG:
                    fragment = SettingsFragment.getInstance();
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
            try {
                // Can not perform this action after onSaveInstanceState Bug
                // solution: commitAllowingStateLoss()
                // see: https://stackoverflow.com/questions/7575921
                ft.replace(R.id.content_frame, fragment, fTag).commitAllowingStateLoss();
            }
            catch (IllegalStateException e){
                // There is still a bug here: "Activity has been destroyed"
                // This may (very probably) be due to using the async task WaitForDb
                // When the user opens the app and rotates the screen at the same time, the activity
                // is created and WaitForDb is executed. Meanwhile, this activity instance is
                // destroyed due to screen rotation. Hence, when the onPostExecute callback of
                // WaitForDb call chooseDisplay() -> chooseModeToDisplay() -> displayFragment()
                // the exception is thrown. <= when/where/why exactly ?
                e.printStackTrace();
            }
        }
    }

    /**
     * Set the toolbar's title
     * @param title the title to be set
     */
    private void setTitle(String title){
        toolBar.setTitle(title);
    }
    //--------------------------------------------------------------------------------------------->
    // Handle Callback Listeners
    //<---------------------------------------------------------------------------------------------


    @Override
    public void onConfirmStartButtonClicked(int catId, int taskId, long startTime) {
        // Start the monitor
        try {
            controller.monitor(catId, taskId, startTime, -1);
        } catch (InvalidCategoryException e){
            // TODO
        } catch (DbErrorException e){
            // TODO
        }
        // Switch the fragment (to OnModeFragment)
        chooseDisplay(0);
    }

    @Override
    public void onConfirmStopButtonClicked(long stopTime) {
        // Stop the monitor
        try {
            // update the monitored task list
            controller.updateMonitoredTaskList();
            // store the last monitored cat/task
            controller.updateLastMonitored();
            // add the monitored block
            controller.monitor(-1, -1, 0, stopTime);
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
    public void onLastMonitoredTaskSelected(int catId, int taskId) {
        ModeOffFragment modeOffFragment = (ModeOffFragment)
                getSupportFragmentManager().findFragmentByTag(MODE_OFF_FRAGMENT_TAG);
        if (modeOffFragment != null){
            modeOffFragment.updateAllSpinner(catId, taskId);
            modeOffFragment.updateStartButtonText(catId, taskId);
            controller.updateLastMonitored(catId, taskId);
        }
    }

    @Override
    public void onConfirmAddingCatButtonClicked(
            CategoryListFragment catListFragment, String newCatName, int priority, String note) {

        try{
            controller.addCategory(newCatName, priority, note);
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
        Toast.makeText(this, "Added new category: " + newCatName, Toast.LENGTH_LONG).show();

        // Update the recyclerView
        catListFragment.updateView(newCatName);


        /* Hard-coded: Re-add the whole CategoryListFragment
        getActivity().getSupportFragmentManager().beginTransaction().
                remove(CategoryListFragment.getInstance()).commit();

        getActivity().getSupportFragmentManager().beginTransaction().add(
                R.id.tasks_list_fragment_container,
                CategoryListFragment.getInstance()).commit();*/

    }

    @Override
    public void onConfirmAddingTaskButtonClicked(
            CategoryRecyclerViewAdapter.ViewHolder viewHolder,
            int catId, String inputTask, int priority, String note) {

        // Add new task
        try{
            controller.addTask(inputTask, catId, priority, note);
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
        Toast.makeText(this, "Added new task: " + inputTask, Toast.LENGTH_LONG).show();

        // Update the recyclerView
        viewHolder.updateView(inputTask, catId);

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
            if(position != 1 && position != 2)
                setTitle(titleList[position]);
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
            while (!PetimoDbWrapper.getInstance().isReady()){
            //while (true){
                try{
                    //Log.d(TAG, "waiting for DB");
                    Thread.sleep(20);
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }

            // Hard-coded here !!!!!! ;)
            // update database
            //PetimoDbWrapper.getInstance().updateV1toV2();
            // printout db
            //PetimoDbWrapper.getInstance().generateXml();
            // update sharedPref
            //SharedPref.getInstance().updateV1toV2();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            chooseDisplay(position);
        }
    }
}
