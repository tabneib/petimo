package de.tud.nhd.petimo.view.activities;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.controller.PetimoController;
import de.tud.nhd.petimo.controller.exception.DbErrorException;
import de.tud.nhd.petimo.controller.exception.InvalidCategoryException;
import de.tud.nhd.petimo.model.db.PetimoDbWrapper;
import de.tud.nhd.petimo.model.sharedpref.PetimoMonitorSPref;
import de.tud.nhd.petimo.model.sharedpref.SharedPref;
import de.tud.nhd.petimo.utils.PetimoContextWrapper;
import de.tud.nhd.petimo.utils.PetimoTimeUtils;
import de.tud.nhd.petimo.view.SlideButton;
import de.tud.nhd.petimo.view.fragments.TaskSelectorBottomSheet;

public class MainActivity extends AppCompatActivity
        implements TaskSelectorBottomSheet.Listener{

    private static final String TAG = "PetimoMainActivity";

    public static final String BOTTOM_SHEET_FRAGMENT_TAG = "BOTTOM_SHEET_FRAGMENT_TAG";

    // arguments TAGs
    private static final String ARG_MANUAL_START_TIME = "ARG_MANUAL_START_TIME";
    private static final String ARG_MANUAL_STOP_TIME = "ARG_MANUAL_STOP_TIME";


    private final int SEEKBAR_THRESHOLD = 80;

    Toolbar toolBar;
    SlideButton mSlider;

    FrameLayout activityLayout;
    ImageView imageMainCircle;
    FrameLayout textViewCatContainer;
    FrameLayout textViewTaskContainer;
    TextView textViewCat;
    TextView textViewTask;
    TextView textViewStartTime;
    TextView textViewStopTime;
    Chronometer chronometer;
    TextView textViewSlider;


    private PetimoController controller;

    private View.OnClickListener selectTaskOnClickListener = null;

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
        //chooseDisplay(triggerActivity);
        //chooseModeToDisplay();


        // debug
        //Log.d(TAG, "gonna run the db demo!");
        //new PetimoDbDemo(this).executeDemo();

        // Init view
        init();

    }

    @Override
    protected void onResume() {
        super.onResume();
        try{
            updateMainCircle();
            updateSlider();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }


    //--------------------------------------------------------------------------------------------->
    //  New Design
    //<---------------------------------------------------------------------------------------------

    /**
     * We have to wait for the db to be ready before initializing stuff
     */
    private void init(){
        if (!PetimoDbWrapper.getInstance().isReady()) {
            new WaitForDb().execute((Void) null);
            return;
        }

        setupSlider();
        setupMainCircle();
    }


    /**
     *
     */
    private void setupSlider(){

        mSlider = (SlideButton) findViewById(R.id.seekBar);
        textViewSlider = (TextView) findViewById(R.id.textView_slider);
        //mSlider.getThumb().mutate().setAlpha(135);

        mSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //seekBar.setThumb(getResources().getDrawable(R.drawable.seekbar_thumb_off,null));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (seekBar.getProgress() == 0) {
                    seekBar.setProgressDrawable(
                            getResources().getDrawable(R.drawable.seekbar_off, null));
                }
                else if (seekBar.getProgress() == 100){
                    seekBar.setProgressDrawable(
                            getResources().getDrawable(R.drawable.seekbar_on, null));
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                if (!PetimoMonitorSPref.getInstance().isMonitoring()){
                    if (seekBar.getProgress() < SEEKBAR_THRESHOLD)  {
                        seekBar.setProgress(0);
                    }
                    else {
                        seekBar.setProgress(seekBar.getMax());
                    }
                }
                else {
                    // Reverse direction
                    if (seekBar.getProgress() > (seekBar.getMax() - SEEKBAR_THRESHOLD))  {
                        seekBar.setProgress(seekBar.getMax());
                    }
                    else {
                        seekBar.setProgress(0);
                    }
                }

                seekBar.setProgressDrawable(getResources().getDrawable(R.drawable.seekbar, null));


                // Stop / Start monitor accordingly
                if (seekBar.getProgress() == 0) {
                    // Stop monitor if any
                    if (PetimoMonitorSPref.getInstance().isMonitoring()){
                        int[] manualTime = getIntent().getIntArrayExtra(ARG_MANUAL_STOP_TIME);
                        if (manualTime != null){
                            stopMonitor(PetimoTimeUtils.getTimeMillisFromHM(
                                    manualTime[0], manualTime[1]));
                        }
                        else{
                            stopMonitor(System.currentTimeMillis());
                        }

                    }
                }
                else if (seekBar.getProgress() == seekBar.getMax()){
                    // Start monitor if not any
                    if (!PetimoMonitorSPref.getInstance().isMonitoring()){
                        int[] manualTime = getIntent().getIntArrayExtra(ARG_MANUAL_START_TIME);
                        if (manualTime != null){
                            startMonitor(PetimoTimeUtils.getTimeMillisFromHM(
                                    manualTime[0], manualTime[1]));
                        }
                        else{
                            startMonitor(System.currentTimeMillis());
                        }
                        updateMainCircle();
                        updateSlider();
                    }
                }
            }
        });
        updateSlider();
    }


    /**
     *
     */
    private void updateSlider(){
        if (PetimoMonitorSPref.getInstance().isMonitoring()){
            // There is on-going monitor
            textViewSlider.setText(getString(R.string.slide_to_stop));
            mSlider.setProgress(mSlider.getMax());
        }
        else {
            // There is no on-going monitor
            textViewSlider.setText(getString(R.string.slide_to_start));
            mSlider.setProgress(0);
        }
    }


    /**
     *
     */
    private void setupMainCircle(){
        activityLayout = (FrameLayout) findViewById(R.id.activity_layout);
        textViewStartTime = (TextView) findViewById(R.id.textView_startTime);
        textViewStopTime = (TextView) findViewById(R.id.textView_stopTime);
        imageMainCircle = (ImageView) findViewById(R.id.image_main_circle);
        textViewCatContainer = (FrameLayout) findViewById(R.id.textView_cat_container);
        textViewTaskContainer = (FrameLayout) findViewById(R.id.textView_task_container);
        textViewCat = (TextView) findViewById(R.id.textView_cat);
        textViewTask = (TextView) findViewById(R.id.textView_task);
        chronometer = (Chronometer) findViewById(R.id.chronometer);


        // un-dim the activity layout
        activityLayout.getForeground().setAlpha(0);

        // Listeners
        selectTaskOnClickListener =
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Only show BottomSheet if not monitoring
                if (!PetimoMonitorSPref.getInstance().isMonitoring()){
                    TaskSelectorBottomSheet bottomSheet = TaskSelectorBottomSheet.newInstance();
                    bottomSheet.show(getSupportFragmentManager(), BOTTOM_SHEET_FRAGMENT_TAG);
                }
            }
        };

        textViewCatContainer.setOnClickListener(selectTaskOnClickListener);
        textViewTaskContainer.setOnClickListener(selectTaskOnClickListener);
        //scrollViewTask.setOnClickListener(selectTaskOnClickListener);


        textViewStartTime.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                // Only show the datePicker if not monitoring
                if (!PetimoMonitorSPref.getInstance().isMonitoring()){
                    Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(getContext(),
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(
                                        TimePicker timePicker, int selectedHour, int selectedMinute) {
                                    // Check if the selected time is valid
                                    if (PetimoController.getInstance().
                                            checkValidLiveStartTime(selectedHour, selectedMinute)){
                                        textViewStartTime.setText(
                                                (selectedHour<10 ? "0" + selectedHour : selectedHour) +
                                                        ":" + (selectedMinute<10 ? "0" + selectedMinute :
                                                        selectedMinute));
                                        // Update manual start time
                                        getIntent().putExtra(ARG_MANUAL_START_TIME,
                                                new int[]{selectedHour, selectedMinute});
                                    }
                                    else
                                        Toast.makeText(getBaseContext(),
                                                getString(R.string.message_invalid_start_time),
                                                Toast.LENGTH_SHORT).show();
                                }
                            }, hour, minute, true);//Yes 24 hour time
                    mTimePicker.setTitle(getString(R.string.title_select_start_time));
                    mTimePicker.show();
                }
            }
        });


        textViewStopTime.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                // Only show the datePicker if monitoring
                if (PetimoMonitorSPref.getInstance().isMonitoring()){
                    Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(getContext(),
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker timePicker,
                                                      int selectedHour, int selectedMinute) {
                                    // Check if the selected time is valid
                                    if (PetimoController.getInstance().
                                            checkValidLiveStopTime(selectedHour, selectedMinute)){
                                        textViewStopTime.setText(
                                                (selectedHour<10 ? "0" + selectedHour : selectedHour) +
                                                        ":" + (selectedMinute<10 ? "0" + selectedMinute
                                                        : selectedMinute));
                                        textViewStopTime.setVisibility(View.VISIBLE);

                                        // Update manual start time
                                        getIntent().putExtra(ARG_MANUAL_STOP_TIME,
                                                new int[]{selectedHour, selectedMinute});
                                    }
                                    else
                                        Toast.makeText(
                                                getContext(), getString(
                                                        R.string.message_invalid_stop_time),
                                                Toast.LENGTH_SHORT).show();
                                }
                            }, hour, minute, true);//Yes 24 hour time
                    mTimePicker.setTitle(getString(R.string.title_select_stop_time));
                    mTimePicker.show();
                }
            }
        });

        updateMainCircle();

    }


    /**
     *
     */
    private void updateMainCircle(){

        //----------------------------------------------------------------------------------------->
        //  Monitoring-dependent
        //<-----------------------------------------------------------------------------------------

        if (PetimoMonitorSPref.getInstance().isMonitoring()){

            // There is odn-going monitor ---------------------------------------------------------->

            final String[] monitorInfo = PetimoController.getInstance().getLiveMonitorInfo();

            // Update the circle
            imageMainCircle.setImageDrawable(getResources().
                    getDrawable(R.drawable.bg_main_circle_on, null));

            // Update Start/Stop Time
            textViewStartTime.setTextColor(getResources().getColor(R.color.textColorSecondary));
            textViewStopTime.setTextColor(getResources().getColor(R.color.colorPrimary));
            ((TextView) findViewById(R.id.stop_header)).setTextColor(
                    getResources().getColor(R.color.background_title_shadow));
            textViewStartTime.setText(monitorInfo[3]);
            int[] manualStop = getIntent().getIntArrayExtra(ARG_MANUAL_STOP_TIME);
            if (manualStop != null)
                textViewStopTime.setText(manualStop[0] + ":" + manualStop[1]);

            // Update cat task textViews
            textViewCat.setTextColor(getResources().getColor(R.color.textColorSecondary));
            textViewTask.setTextColor(getResources().getColor(R.color.textColorSecondary));
            setCatText(PetimoDbWrapper.getInstance().getCatNameById(
                    PetimoMonitorSPref.getInstance().getLastMonitoredTask()[0]));
            setTaskText(PetimoDbWrapper.getInstance().getTaskNameById(
                    PetimoMonitorSPref.getInstance().getLastMonitoredTask()[1]));
            textViewCatContainer.setVisibility(View.VISIBLE);


            // Chronometer is showing
            chronometer.setVisibility(View.VISIBLE);
            long passedTime = System.currentTimeMillis() - Long.parseLong(monitorInfo[4]);
            chronometer.setBase(SystemClock.elapsedRealtime() - passedTime);
            chronometer.start();

        }
        else {
            // No On-going monitor ---------------------------------------------------------------->

            // Update the circle
            imageMainCircle.setImageDrawable(getResources().
                    getDrawable(R.drawable.bg_main_circle_off, null));

            // Update Start/Stop Time
            textViewStartTime.setTextColor(getResources().getColor(R.color.colorPrimary));
            textViewStopTime.setTextColor(getResources().getColor(R.color.background_title));
            textViewStartTime.setText(getString(R.string.now));
            textViewStopTime.setText(getString(R.string.now));
            ((TextView) findViewById(R.id.stop_header)).setTextColor(
                    getResources().getColor(R.color.background_title));


            // Update cat task textViews
            textViewCat.setTextColor(getResources().getColor(R.color.colorPrimary));
            textViewTask.setTextColor(getResources().getColor(R.color.colorPrimary));
            // Update the last cat/task accordingly
            String lastCat = PetimoDbWrapper.getInstance().getCatNameById(
                    PetimoMonitorSPref.getInstance().getLastMonitoredTask()[0]);
            String lastTask = PetimoDbWrapper.getInstance().getTaskNameById(
                    PetimoMonitorSPref.getInstance().getLastMonitoredTask()[1]);
            if (lastCat != null && lastTask != null){
                textViewCatContainer.setVisibility(View.VISIBLE);
                setCatText(lastCat);
                setTaskText(lastTask);
            }
            else {
                setTaskText(getString(R.string.select_task));
                textViewCatContainer.setVisibility(View.INVISIBLE);
            }

            // Chronometer not showing
            chronometer.setVisibility(View.INVISIBLE);


        }

    }



    //--------------------------------------------------------------------------------------------->
    // Auxiliary
    //<---------------------------------------------------------------------------------------------

    /**
     * This programmatically adjust the alignment of the textView that displays task name
     * @param text
     */
    private void setTaskText(String text){
        if (text.length() < 17) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.CENTER;
            textViewTaskContainer.setLayoutParams(params);
            textViewTask.setText(text);
        } else {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.NO_GRAVITY;
            textViewTaskContainer.setLayoutParams(params);
            textViewTask.setText(text);
        }
        // Refresh the layout of the textview. This force recomputing the width of the textView
        ViewGroup.LayoutParams tvParams = textViewTask.getLayoutParams();
        tvParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        textViewTask.setLayoutParams(tvParams);
        textViewTask.invalidate();
    }


    /**
     * This programmatically adjust the alignment of the textView that displays cat name
     * @param text
     */
    private void setCatText(String text){
        if (text.length() < 17) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.CENTER;
            textViewCatContainer.setLayoutParams(params);
            textViewCat.setText(text);
        } else {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.NO_GRAVITY;
            textViewCatContainer.setLayoutParams(params);
            textViewCat.setText(text);
        }
        // Refresh the layout of the textview. This force recomputing the width of the textView
        ViewGroup.LayoutParams tvParams = textViewCat.getLayoutParams();
        tvParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        textViewCat.setLayoutParams(tvParams);
        textViewCat.invalidate();
    }


    /**
     *
     * @param startTime
     */
    private void startMonitor(long startTime){
            // Start the monitor
            try {
                controller.monitor(PetimoMonitorSPref.getInstance().getLastMonitoredTask()[0],
                        PetimoMonitorSPref.getInstance().getLastMonitoredTask()[1], startTime, -1);
            } catch (InvalidCategoryException e){
                // TODO
            } catch (DbErrorException e){
                // TODO
            }
    }

    /**
     *
     * @param stopTime
     */
    private void stopMonitor(long stopTime){
        // Reset Start and End Time
        getIntent().removeExtra(ARG_MANUAL_START_TIME);
        getIntent().removeExtra(ARG_MANUAL_STOP_TIME);

        try {
            // update the monitored task list
            controller.updateMonitoredTaskList();
            // store the last monitored cat/task
            //controller.updateLastMonitored();
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

    Context getContext(){
        return this;
    }


    //--------------------------------------------------------------------------------------------->
    // Handle Callback Listeners
    //<---------------------------------------------------------------------------------------------

    @Override
    public void onTaskSelected(int catId, int taskId) {
        // Update view
        textViewCat.setVisibility(View.VISIBLE);
        setCatText(PetimoDbWrapper.getInstance().getCatNameById(catId));
        setTaskText(PetimoDbWrapper.getInstance().getTaskNameById(taskId));

        TaskSelectorBottomSheet bottomSheet = (TaskSelectorBottomSheet)
                getSupportFragmentManager().findFragmentByTag(BOTTOM_SHEET_FRAGMENT_TAG);
        if (bottomSheet != null)
            bottomSheet.dismiss();

        // Notify Controller
        controller.updateLastMonitored(catId, taskId);
    }


    //--------------------------------------------------------------------------------------------->
    //  Drawer
    //<---------------------------------------------------------------------------------------------

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
     * DONE: step by step change to use activity instead of fragment !
     * @param position
     */
    private void triggerActivity(int position){

        Intent intent;
        switch (position) {
            case 0:
                intent = new Intent(this, EditBlockActivity.class);
                startActivity(intent);
                return;
            case 1:
                intent = new Intent(this, StatisticsActivity.class);
                startActivity(intent);
                return;
            case 2:
                intent = new Intent(this, EditTasksActivity.class);
                startActivity(intent);
                return;
            case 3:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return;

            /*
            case MODE_OFF_FRAGMENT_TAG:
                fragment = ModeOffFragment.getInstance();
                break;
            case MODE_ON_FRAGMENT_TAG:
                fragment = ModeOnFragment.getInstance();
                break; */

            /*
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
                // WaitForDb call chooseDisplay() -> chooseModeToDisplay() -> triggerActivity()
                // the exception is thrown. <= when/where/why exactly ?
                e.printStackTrace();
            }*/
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (drawerToggle.onOptionsItemSelected(item))
            return true;

        // Handle other action bar items..
        return super.onOptionsItemSelected(item);
    }

    /**
     *
     */
    private class OnDrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            triggerActivity(position);
            // Close the drawer
            drawerLayout.closeDrawer(drawerList);
            /*if(position != 1 && position != 2)
                setTitle(titleList[position]);*/
        }
    }




    /**
     * Busy loop until the db wrapper is ready. This is only done when the app is starting up
     */
    private class WaitForDb extends AsyncTask<Void, Void, Void> {

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
            init();
        }
    }
}
