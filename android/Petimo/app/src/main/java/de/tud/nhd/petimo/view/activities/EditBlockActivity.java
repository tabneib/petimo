package de.tud.nhd.petimo.view.activities;

import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.Switch;

import java.util.Calendar;
import java.util.Date;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.controller.PetimoController;
import de.tud.nhd.petimo.model.db.MonitorBlock;
import de.tud.nhd.petimo.model.sharedpref.TaskSelector;
import de.tud.nhd.petimo.model.sharedpref.PetimoSPref;
import de.tud.nhd.petimo.model.sharedpref.PetimoSettingsSPref;
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


    ImageView overFlowIcon;
    FrameLayout listContainer;
    FrameLayout activityLayout;

    PopupWindow popupWindow;
    Switch switchSelectedTask;
    Switch switchEmptyDays;
    Switch switchSwipeToDel;
    RadioButton radioGroupedByTask;
    RadioButton radioGroupedByCat;
    RadioButton radioNotGrouped;

    private boolean updateDayList = false;
    private boolean reloadDayList = false;
    private boolean recreateDayListFragment = false;

    Calendar fromCalendar = Calendar.getInstance();
    Calendar toCalendar = Calendar.getInstance();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_block);

        activityLayout = (FrameLayout) findViewById(R.id.activity_layout);
        activityLayout.getForeground().setAlpha(0);

        // Toolbar
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
                R.id.menu_container, PetimoDatePickerMenu.newInstance(true),
                MENU_FRAGMENT_TAG).commit();


        setupPopupWindow();
    }


    /**
     *
     */
    private void setupPopupWindow(){
        overFlowIcon = (ImageView) findViewById(R.id.overflow);

        // Inflate the popup window
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View mView = layoutInflater.inflate(R.layout.popup_menu_monitored_tasks, null);
        popupWindow = new PopupWindow(mView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);

        // Hack to dismiss the popup by clicking outside
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.setOutsideTouchable(true);

        popupWindow.setAnimationStyle(R.style.PetimoThemePopupWindow);

        overFlowIcon.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                // hide the menu
                PetimoDatePickerMenu menu = (PetimoDatePickerMenu)
                        getSupportFragmentManager().findFragmentByTag(MENU_FRAGMENT_TAG);
                if (menu != null)
                    menu.deactivate(new PetimoDatePickerMenu.PostTask() {
                        @Override
                        public void execute() {
                            // Dim the activity
                            activityLayout.getForeground().setAlpha(130);
                            popupWindow.showAtLocation(activityLayout, Gravity.BOTTOM, 0, 0);
                        }
                    });
            }
        });

        switchSelectedTask = (Switch) mView.findViewById(R.id.switch_selected_tasks);
        switchEmptyDays = (Switch) mView.findViewById(R.id.switch_show_empty_days);
        switchSwipeToDel = (Switch) mView.findViewById(R.id.switch_swipe_to_delete);

        radioGroupedByTask = (RadioButton) mView.findViewById(R.id.radioButton_grouped_by_task);
        radioGroupedByCat = (RadioButton) mView.findViewById(R.id.radioButton_grouped_by_cat);
        radioNotGrouped = (RadioButton) mView.findViewById(R.id.radioButton_not_grouped);

        updateChecked();

        switchSelectedTask.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateDayList = true;
                PetimoSettingsSPref.getInstance().putBoolean(
                        PetimoSettingsSPref.MONITORED_BLOCKS_SHOW_SELECTED_TASKS,
                        isChecked);

                // Display task selector dialog if checked
                if (isChecked){
                    TaskSelector.getInstance().startTransaction(PetimoSPref.Consts.EDIT_BLOCK);
                    CategoryListFragment catListFragment = CategoryListFragment.
                            getInstance(CategoryListFragment.SELECT_MODE,
                                    PetimoSPref.Consts.EDIT_BLOCK);
                    PetimoDialog taskSelectorDialog =
                            PetimoDialog.newInstance(getBaseContext(), true)
                                    .setSelectorMode(PetimoSPref.Consts.EDIT_BLOCK)
                                    .setIcon(PetimoDialog.ICON_SAVE)
                                    .setTitle(getString(R.string.title_select_tasks_to_display))
                                    .setContentFragment(catListFragment)
                                    .setPositiveButton(getString(R.string.button_ok),
                                            new PetimoDialog.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    TaskSelector.getInstance().commit();
                                                    // Update Day List
                                                    //updateDayList();
                                                }
                                            });
                    taskSelectorDialog.show(getSupportFragmentManager(), null);
                }
            }
        });

        switchEmptyDays.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateDayList = true;
                PetimoSettingsSPref.getInstance().putBoolean(
                        PetimoSettingsSPref.MONITORED_BLOCKS_SHOW_EMPTY_DAYS, isChecked);
                // Update Day List
                //updateDayList();
            }
        });

        switchSwipeToDel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                reloadDayList = true;
                PetimoSettingsSPref.getInstance().putBoolean(
                        PetimoSettingsSPref.MONITORED_BLOCKS_LOCK, isChecked);
                // Update Day List & Adapter
                //reloadDayList();
            }
        });

        radioGroupedByTask.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                recreateDayListFragment = true;
                if (isChecked)
                    PetimoSettingsSPref.getInstance().putString(
                        PetimoSettingsSPref.MONITORED_BLOCKS_GROUP_BY,
                        PetimoSPref.Consts.GROUP_BY_TASK);
            }
        });

        radioGroupedByCat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                recreateDayListFragment = true;
                if (isChecked)
                    PetimoSettingsSPref.getInstance().putString(
                        PetimoSettingsSPref.MONITORED_BLOCKS_GROUP_BY,
                        PetimoSPref.Consts.GROUP_BY_CAT);
            }
        });

        radioNotGrouped.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                recreateDayListFragment = true;
                if (isChecked)
                    PetimoSettingsSPref.getInstance().putString(
                        PetimoSettingsSPref.MONITORED_BLOCKS_GROUP_BY,
                        PetimoSPref.Consts.NOT_GROUP);
            }
        });


        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (updateDayList){
                    updateDayList();
                    updateDayList = false;
                }
                if (reloadDayList){
                    reloadDayList();
                    reloadDayList = false;
                }
                if (recreateDayListFragment){
                    recreateDayListFragment();
                    recreateDayListFragment = false;
                }

                // un-dim the activity
                activityLayout.getForeground().setAlpha(0);

                // restore the menu
                PetimoDatePickerMenu menu = (PetimoDatePickerMenu)
                        getSupportFragmentManager().findFragmentByTag(MENU_FRAGMENT_TAG);
                if (menu != null)
                    menu.reactivate();
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.d(TAG, "onResume!");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

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
        if (PetimoSettingsSPref.getInstance().getSettingsBoolean(
                PetimoSettingsSPref.MONITORED_BLOCKS_SHOW_SELECTED_TASKS, false))
            switchSelectedTask.setChecked(true);

        if (PetimoSettingsSPref.getInstance().getSettingsBoolean(
                PetimoSettingsSPref.MONITORED_BLOCKS_SHOW_EMPTY_DAYS, false))
            switchEmptyDays.setChecked(true);

        if (PetimoSettingsSPref.getInstance().getSettingsBoolean(
                PetimoSettingsSPref.MONITORED_BLOCKS_LOCK, false)){
            switchSwipeToDel.setChecked(true);
        }

        switch (PetimoSettingsSPref.getInstance().getSettingsString(
                PetimoSettingsSPref.MONITORED_BLOCKS_GROUP_BY, PetimoSPref.Consts.NOT_GROUP)){
            case PetimoSPref.Consts.GROUP_BY_CAT:
                radioGroupedByCat.setChecked(true);
                break;
            case PetimoSPref.Consts.GROUP_BY_TASK:
                radioGroupedByTask.setChecked(true);
                break;
            case PetimoSPref.Consts.NOT_GROUP:
                radioNotGrouped.setChecked(true);
                break;
        }
    }


    /**
     * Force the recyclerView to rebind all items
     */
    public void updateDayList(){
        DayListFragment dayListFragment = (DayListFragment)
                getSupportFragmentManager().findFragmentByTag(DAY_LIST_FRAGMENT_TAG);
        if(dayListFragment != null){
            dayListFragment.dayAdapter.dayList = PetimoController.getInstance().
                    getDaysFromRange(PetimoSPref.Consts.EDIT_BLOCK,
                            fromCalendar, toCalendar,
                            PetimoSettingsSPref.getInstance().getSettingsBoolean(PetimoSettingsSPref.
                                    MONITORED_BLOCKS_SHOW_EMPTY_DAYS, true),
                            PetimoSettingsSPref.getInstance().getSettingsBoolean(PetimoSettingsSPref.
                                    MONITORED_BLOCKS_SHOW_SELECTED_TASKS, false));
            dayListFragment.dayAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Use when user locks the swipe to delete option
     */
    public void reloadDayList(){
        DayListFragment dayListFragment = (DayListFragment)
                getSupportFragmentManager().findFragmentByTag(DAY_LIST_FRAGMENT_TAG);
        if(dayListFragment != null) {
            dayListFragment.dayAdapter.notifyDataSetChanged();
        }
    }

    private void recreateDayListFragment(){
        DayListFragment dayListFragment = (DayListFragment)
                getSupportFragmentManager().findFragmentByTag(DAY_LIST_FRAGMENT_TAG);
        if (dayListFragment != null)
            getSupportFragmentManager().beginTransaction().remove(dayListFragment).commit();

        getSupportFragmentManager().beginTransaction().add(
                R.id.day_list_fragment_container, DayListFragment.newInstance(),
                DAY_LIST_FRAGMENT_TAG).commit();
    }

    @Override
    public void onDateChanged(Calendar fromCalendar, Calendar toCalendar) {
        this.fromCalendar = fromCalendar;
        this.toCalendar = toCalendar;
        updateDayList();
    }

    // TODO: what is this??? remove it!
    @Override
    public void onRemovingMonitorBlock(MonitorBlock item) {

    }
}
