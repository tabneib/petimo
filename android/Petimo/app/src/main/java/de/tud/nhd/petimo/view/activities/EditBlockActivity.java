package de.tud.nhd.petimo.view.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import java.util.Calendar;
import java.util.Date;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.controller.PetimoController;
import de.tud.nhd.petimo.model.db.MonitorBlock;
import de.tud.nhd.petimo.model.sharedpref.SharedPref;
import de.tud.nhd.petimo.utils.PetimoTimeUtils;
import de.tud.nhd.petimo.view.fragments.dialogs.PetimoDialog;
import de.tud.nhd.petimo.view.fragments.lists.CategoryListFragment;
import de.tud.nhd.petimo.view.fragments.lists.DayListFragment;
import de.tud.nhd.petimo.view.fragments.menu.PetimoDatePickerMenu;
import de.tud.nhd.petimo.view.fragments.lists.DayListFragment.OnEditDayFragmentInteractionListener;

public class EditBlockActivity extends AppCompatActivity
        implements PetimoDatePickerMenu.OnDateRangeChangeListener,
        OnEditDayFragmentInteractionListener{

    private static final String TAG = "EditBlocksActivity";
    private static final String DAY_LIST_FRAGMENT_TAG = TAG + "-DayListFragment";
    private static final String MENU_FRAGMENT_TAG = TAG + "-menu";


    FrameLayout listContainer;
    MenuItem mItemSelectedTask;
    MenuItem mItemEmptyDays;
    MenuItem mItemSwipeToDel;

    Calendar fromCalendar = Calendar.getInstance();
    Calendar toCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_block);

        // Overflow menu
        Toolbar mToolbar = (Toolbar) findViewById(R.id.activity_editblock_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listContainer = (FrameLayout) findViewById(R.id.day_list_fragment_container);

        // default date range is the last 1 week
        fromCalendar.setTime(new Date());
        toCalendar.setTime(new Date());
        fromCalendar.add(Calendar.DATE, -6);

        // Attach the fragment to display day list
        getSupportFragmentManager().beginTransaction().add(
                R.id.day_list_fragment_container, DayListFragment.newInstance(),
                DAY_LIST_FRAGMENT_TAG).commit();

        // Attach the menu fragment
        getSupportFragmentManager().beginTransaction().add(
                R.id.menu_container, PetimoDatePickerMenu.newInstance(),
                MENU_FRAGMENT_TAG).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mItemSelectedTask = menu.findItem(R.id.show_selected_tasks);
        mItemEmptyDays = menu.findItem(R.id.show_empty_days);
        mItemSwipeToDel = menu.findItem(R.id.swipe_to_delete);
        updateChecked();
        //menu.findItem(R.id.show_selected_tasks).setChecked(true);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_selected_tasks:

                // Negate the checked status
                item.setChecked(!item.isChecked());

                SharedPref.getInstance().setSettingsBoolean(
                        SharedPref.SETTINGS_MONITORED_BLOCKS_SHOW_SELECTED_TASKS,
                        item.isChecked());

                // Display task selector dialog if checked
                if (item.isChecked()){
                            /*CatTaskListFragment taskListFragment =
                                    CatTaskListFragment.newInstance();*/
                    CategoryListFragment catListFragment = CategoryListFragment.
                            getInstance(CategoryListFragment.SELECT_MODE);
                    PetimoDialog taskSelectorDialog =
                            PetimoDialog.newInstance(this, true)
                                    .setIcon(PetimoDialog.ICON_SAVE)
                                    .setTitle(getString(R.string.title_select_tasks_to_display))
                                    .setContentFragment(catListFragment)
                                    .setPositiveButton(getString(R.string.button_ok),
                                            new PetimoDialog.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    // Update Day List
                                                    updateDayList();
                                                }
                                            });
                    taskSelectorDialog.show(getSupportFragmentManager(), null);
                }
                else
                    updateDayList();
                return true;

            case R.id.show_empty_days:
                // Negate the checked status
                item.setChecked(!item.isChecked());
                SharedPref.getInstance().setSettingsBoolean(
                        SharedPref.SETTINGS_MONITORED_BLOCKS_SHOW_EMPTY_DAYS,
                        item.isChecked());
                // Update Day List
                updateDayList();
                return true;

            case R.id.swipe_to_delete:
                // Negate the checked status
                item.setChecked(!item.isChecked());
                SharedPref.getInstance().setSettingsBoolean(
                        SharedPref.SETTINGS_MONITORED_BLOCKS_LOCK, item.isChecked());
                // Update Day List & Adapter
                reloadDayList();
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }

    /**
     * Update the checked state of overflow menu items
     */
    private void updateChecked(){
        if (SharedPref.getInstance().getSettingsBoolean(
                SharedPref.SETTINGS_MONITORED_BLOCKS_SHOW_SELECTED_TASKS, false))
            mItemSelectedTask.setChecked(true);
        if (SharedPref.getInstance().getSettingsBoolean(
                SharedPref.SETTINGS_MONITORED_BLOCKS_SHOW_EMPTY_DAYS, false))
            mItemEmptyDays.setChecked(true);


        if (SharedPref.getInstance().getSettingsBoolean(
                SharedPref.SETTINGS_MONITORED_BLOCKS_LOCK, false)){
            mItemSwipeToDel.setChecked(true);
        }
    }


    /**
     * Force the recyclerView to rebind all items
     */
    public void updateDayList(){
        DayListFragment dayListFragment = (DayListFragment)
                getSupportFragmentManager().findFragmentByTag(DAY_LIST_FRAGMENT_TAG);
        if(dayListFragment != null){
            dayListFragment.adapter.dayList = PetimoController.getInstance().
                    getDaysFromRange(PetimoTimeUtils.getDateIntFromCalendatr(fromCalendar),
                            PetimoTimeUtils.getDateIntFromCalendatr(toCalendar),
                            SharedPref.getInstance().getSettingsBoolean(SharedPref.
                                    SETTINGS_MONITORED_BLOCKS_SHOW_EMPTY_DAYS, true),
                            SharedPref.getInstance().getSettingsBoolean(SharedPref.
                                    SETTINGS_MONITORED_BLOCKS_SHOW_SELECTED_TASKS, false));
            dayListFragment.adapter.notifyDataSetChanged();
        }
    }

    /**
     * Use when user locks the swipe to delete option
     */
    public void reloadDayList(){
        DayListFragment dayListFragment = (DayListFragment)
                getSupportFragmentManager().findFragmentByTag(DAY_LIST_FRAGMENT_TAG);
        if(dayListFragment != null) {
            dayListFragment.adapter.notifyDataSetChanged();
        }
    }



    @Override
    public void onDateChanged(Calendar fromCalendar, Calendar toCalendar) {
        this.fromCalendar = fromCalendar;
        this.toCalendar = toCalendar;
        //Toast.makeText(this, "From: " + PetimoTimeUtils.getDateStrFromCalendar(this.fromCalendar) +
        //         "  -  To: " + PetimoTimeUtils.getDateStrFromCalendar(this.toCalendar), Toast.LENGTH_SHORT).show();
        updateDayList();
    }

    // TODO: what is this??? remove it!
    @Override
    public void onRemovingMonitorBlock(MonitorBlock item) {

    }
}
