package de.tud.nhd.petimo.view.activities;

import android.animation.Animator;
import android.app.DatePickerDialog;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.Switch;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.controller.PetimoController;
import de.tud.nhd.petimo.model.chart.PetimoLineData;
import de.tud.nhd.petimo.model.db.MonitorDay;
import de.tud.nhd.petimo.model.db.PetimoDbWrapper;
import de.tud.nhd.petimo.model.sharedpref.PetimoSPref;
import de.tud.nhd.petimo.model.sharedpref.PetimoSettingsSPref;
import de.tud.nhd.petimo.model.sharedpref.TaskSelector;
import de.tud.nhd.petimo.utils.PetimoTimeUtils;
import de.tud.nhd.petimo.view.fragments.ChartFragment;
import de.tud.nhd.petimo.view.fragments.dialogs.PetimoDialog;
import de.tud.nhd.petimo.view.fragments.lists.CategoryListFragment;

/**
 * DONE: customized IAxisValueFormatter for displaying monitor dates
 * DONE: Input for Chart: Update MonitorDay code | DatePickers
 * TODO: Chart Fragments - refreshable | Async Task | Modular Data Inputting
 * TODO: Overflow menu: Full Screen | Show selected tasks, Show selected categories | Line Chart, Bar Chart, Pie Chart | New Customized Report | Export As File
 */
public class StatisticsActivity extends AppCompatActivity
        implements ChartFragment.ChartDataProvider{

    public static final String TAG = "StatisticsActivity";
    public static final String LINE_CHART_FRAGMENT_TAG = TAG + "LineChart-Fragment";

    private ChartFragment lineChartFragment;
    private PopupWindow popupWindow;
    private CardView menuButtonContainer;
    private ImageButton menuButton;
    private FrameLayout activityLayout;

    Button fromDateButton;
    Button toDateButton;
    RadioButton radioTask;
    RadioButton radioCat;
    Switch switchShowSelected;
    Switch switchShowSumLine;
    Switch switchPinchZoom;

    private Calendar toCalendar;
    private Calendar fromCalendar;
    private final int DEFAULT_DATE_RANGE = 7;
    private final int ANIMATION_SPEED = 200;

    private boolean chartDataChanged = false;
    private String chartSettingsChanged = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_statistics);

        activityLayout = (FrameLayout) findViewById(R.id.activity_layout);
        // un-dim
        activityLayout.getForeground().setAlpha(0);

        fromCalendar = PetimoTimeUtils.getTodayCalendar();
        toCalendar = PetimoTimeUtils.getTodayCalendar();
        fromCalendar.add(Calendar.DATE, -1 * (DEFAULT_DATE_RANGE - 1));

        setupPopupWindow();

        lineChartFragment = (ChartFragment)
                getSupportFragmentManager().findFragmentByTag(LINE_CHART_FRAGMENT_TAG);
        if (lineChartFragment == null){
            lineChartFragment = ChartFragment.newInstance(ChartFragment.ChartType.LINE_CHART);
            getSupportFragmentManager().beginTransaction().add(
                    R.id.chart_container, lineChartFragment, LINE_CHART_FRAGMENT_TAG).commit();
        }
        else{
            getSupportFragmentManager().beginTransaction().replace(
                    R.id.chart_container, lineChartFragment, LINE_CHART_FRAGMENT_TAG).commit();
        }

    }

    /**
     *
     */
    private void setupPopupWindow(){

        // Styling the menuButton
        menuButtonContainer = (CardView) findViewById(R.id.menuButtonContainer);
        menuButton = (ImageButton) findViewById(R.id.menuButton);
        menuButtonContainer.getBackground().setAlpha(100);
        menuButton.setAlpha(0.5f);

        // Inflate the popup window
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View mView = layoutInflater.inflate(R.layout.popup_menu_statistics, null);
        popupWindow = new PopupWindow(mView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);

        // Hack to dismiss the popup by clicking outside
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.setOutsideTouchable(true);

        popupWindow.setAnimationStyle(R.style.PetimoThemePopupWindow);

        View.OnClickListener openPopup = new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // hide the MenuButton. Afterwards the popupMenu will be shown
                hideMenuButton();
            }
        };

        menuButtonContainer.setOnClickListener(openPopup);
        menuButton.setOnClickListener(openPopup);

        radioCat = (RadioButton) mView.findViewById(R.id.radio_cats);
        radioTask = (RadioButton) mView.findViewById(R.id.radio_tasks);
        switchShowSelected = (Switch) mView.findViewById(R.id.switch_only_selected);
        switchShowSumLine = (Switch) mView.findViewById(R.id.switch_view_sum);
        switchPinchZoom = (Switch) mView.findViewById(R.id.switch_pinch_zoom);

        updateChecked();


        // DatePicker
        fromDateButton = (Button) mView.findViewById(R.id.button_date_from);
        toDateButton = (Button) mView.findViewById(R.id.button_date_to);

        fromDateButton.setText(PetimoTimeUtils.getDateStrFromCalendar(fromCalendar));
        toDateButton.setText(PetimoTimeUtils.getDateStrFromCalendar(toCalendar));

        setListeners();

    }


    /**
     *
     */
    private void updateChecked(){
        switch (PetimoSettingsSPref.getInstance().getString(
                PetimoSettingsSPref.STATISTICS_GROUP_BY, PetimoSPref.Consts.GROUP_BY_TASK)) {
            case PetimoSPref.Consts.GROUP_BY_TASK:
                this.radioTask.setChecked(true);
                this.switchShowSelected.setChecked(PetimoSettingsSPref.getInstance().
                        getBoolean(PetimoSettingsSPref.STATISTICS_SHOW_SELECTED_TASKS,
                                false));
                updateSwitchText();
                break;
            case PetimoSPref.Consts.GROUP_BY_CAT:
                this.radioCat.setChecked(true);
                this.switchShowSelected.setChecked(PetimoSettingsSPref.getInstance().
                        getBoolean(PetimoSettingsSPref.STATISTICS_SHOW_SELECTED_CATS,
                                false));
                updateSwitchText();
                break;
            default:
                throw new RuntimeException("Unknown grouping mode!");
        }

        switchShowSumLine.setChecked(PetimoSettingsSPref.getInstance().getBoolean(
                PetimoSettingsSPref.STATISTICS_SHOW_SUM_LINE, true));
        switchPinchZoom.setChecked(PetimoSettingsSPref.getInstance().getBoolean(
                PetimoSettingsSPref.STATISTICS_ENABLE_PINCH_ZOOM, false));
    }


    /**
     *
     */
    private void setListeners(){

        // Options -------------------------------------------------------------------------------->

        radioTask.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Store the user's choice
                PetimoSettingsSPref.getInstance().putBoolean(
                        PetimoSettingsSPref.STATISTICS_SHOW_SELECTED_TASKS, isChecked);
                PetimoSettingsSPref.getInstance().putBoolean(
                        PetimoSettingsSPref.STATISTICS_SHOW_SELECTED_CATS, !isChecked);
                // Update the switch's text
                updateSwitchText();

                // to update the Chart due to data change
                chartDataChanged = true;
            }
        });

        radioCat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //TODO
                // Store the user's choice
                PetimoSettingsSPref.getInstance().putBoolean(
                        PetimoSettingsSPref.STATISTICS_SHOW_SELECTED_CATS, isChecked);
                PetimoSettingsSPref.getInstance().putBoolean(
                        PetimoSettingsSPref.STATISTICS_SHOW_SELECTED_TASKS, !isChecked);
                // Update the switch's text
                updateSwitchText();

                // to update the Chart due to data change
                chartDataChanged = true;
            }
        });

        switchShowSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                PetimoSettingsSPref.getInstance().putBoolean(
                        PetimoSettingsSPref.STATISTICS_SHOW_SELECTED_TASKS, isChecked);

                // Display task selector dialog if checked
                if (isChecked){
                    TaskSelector.getInstance().startTransaction(PetimoSPref.Consts.STATISTICS);
                    CategoryListFragment catListFragment = CategoryListFragment.
                            getInstance(CategoryListFragment.SELECT_MODE,
                                    PetimoSPref.Consts.STATISTICS);
                    PetimoDialog taskSelectorDialog =
                            PetimoDialog.newInstance(getBaseContext(), true)
                                    .setSelectorMode(PetimoSPref.Consts.STATISTICS)
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

                // to update the Chart due to data change
                chartDataChanged = true;
            }
        });

        switchShowSumLine.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PetimoSettingsSPref.getInstance().putBoolean(
                        PetimoSettingsSPref.STATISTICS_SHOW_SUM_LINE, isChecked);

                // to update the Chart due to data change
                chartDataChanged = true;
            }
        });

        switchPinchZoom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PetimoSettingsSPref.getInstance().putBoolean(
                        PetimoSettingsSPref.STATISTICS_ENABLE_PINCH_ZOOM, isChecked);

                // to update the corresponding Chart Settings
                chartSettingsChanged = ChartFragment.ChartSettings.PINCH_ZOOM;
            }
        });



        // DatePickers ---------------------------------------------------------------------------->

        fromDateButton.setOnClickListener(new View.OnClickListener(){

            DatePickerDialog.OnDateSetListener onDateSetListener =
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month,
                                              int dayOfMonth) {
                            fromCalendar.set(Calendar.YEAR, year);
                            fromCalendar.set(Calendar.MONTH, month);
                            fromCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            // TODO Check for valid fromDate according to toDate !
                            // to update the Chart due to data change
                            chartDataChanged = true;

                            // Update the fromButton accordingly to display the date
                            fromDateButton.setText(PetimoTimeUtils.getDateStrFromCalendar(fromCalendar));
                        }
                    };

            @Override
            public void onClick(View v) {
                new DatePickerDialog(v.getContext(), onDateSetListener, fromCalendar
                        .get(Calendar.YEAR), fromCalendar.get(Calendar.MONTH),
                        fromCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        toDateButton.setOnClickListener(new View.OnClickListener(){

            DatePickerDialog.OnDateSetListener onDateSetListener =
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int month, int dayOfMonth) {
                            toCalendar.set(Calendar.YEAR, year);
                            toCalendar.set(Calendar.MONTH, month);
                            toCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            // to update the Chart due to data change
                            chartDataChanged = true;
                            // Update the toButton accordingly to display the date
                            toDateButton.setText(PetimoTimeUtils.getDateStrFromCalendar(toCalendar));

                        }
                    };
            @Override
            public void onClick(View v) {
                new DatePickerDialog(v.getContext(), onDateSetListener, toCalendar
                        .get(Calendar.YEAR), toCalendar.get(Calendar.MONTH),
                        toCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });



        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

                if (chartDataChanged) {
                    chartDataChanged = false;
                    // Invalidate the chart
                    ChartFragment chartFragment = (ChartFragment)
                            getSupportFragmentManager().findFragmentByTag(LINE_CHART_FRAGMENT_TAG);
                    if (chartFragment != null)
                        chartFragment.invalidateChart(false);
                }

                if (chartSettingsChanged != null){
                    ChartFragment chartFragment = (ChartFragment)
                            getSupportFragmentManager().findFragmentByTag(LINE_CHART_FRAGMENT_TAG);
                    if (chartFragment != null)
                        chartFragment.updateSettings(chartSettingsChanged);
                    chartSettingsChanged = null;
                }

                // un-dim the activity
                activityLayout.getForeground().setAlpha(0);

                // Display the menubutton
                displayMenuButton();
            }
        });

    }

    private void updateSwitchText(){
        // Update the Switch's text
        switch (PetimoSettingsSPref.getInstance().getString(
                PetimoSettingsSPref.STATISTICS_GROUP_BY, PetimoSPref.Consts.GROUP_BY_TASK)) {
            case PetimoSPref.Consts.GROUP_BY_TASK:
                switchShowSelected.setText(getString(R.string.option_show_selected_tasks));
                break;
            case PetimoSPref.Consts.GROUP_BY_CAT:
                switchShowSelected.setText(getString(R.string.option_show_selected_categories));
                break;
        }
    }

    /**
     *
     */
    private void hideMenuButton(){
        menuButtonContainer.animate().translationX(menuButtonContainer.getWidth()).
                setDuration(ANIMATION_SPEED).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // Dim the activity
                activityLayout.getForeground().setAlpha(130);
                popupWindow.showAtLocation(activityLayout, Gravity.BOTTOM, 0, 0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    /**
     *
     */
    private void displayMenuButton(){
        menuButtonContainer.animate().translationX(0).setDuration(ANIMATION_SPEED * 3).
                setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }


    @Override
    public PetimoLineData getData() {

        ArrayList<Integer> dates = PetimoTimeUtils.getDateIntFromRange(fromCalendar, toCalendar);
        PetimoLineData data = new PetimoLineData(dates);
        float maxYValue = 0;

        // TODO: Next Step is SHOW SELECTED CATS
        ArrayList<MonitorDay> days = PetimoController.getInstance().
                getDaysFromRange(PetimoSPref.Consts.STATISTICS,
                        fromCalendar, toCalendar, true,
                        PetimoSettingsSPref.getInstance().getBoolean(
                                PetimoSettingsSPref.STATISTICS_SHOW_SELECTED_TASKS, false));

        ArrayList<Integer> tasks;
        if (PetimoSettingsSPref.getInstance().getBoolean(
                PetimoSettingsSPref.STATISTICS_SHOW_SELECTED_TASKS, false))
            tasks = TaskSelector.getInstance().getSelectedTasks(PetimoSPref.Consts.STATISTICS);
        else
            tasks = PetimoDbWrapper.getInstance().getAllTaskIds();

        // First, add the sum line if required
        int i = 0;
        if (PetimoSettingsSPref.getInstance().getBoolean(
                PetimoSettingsSPref.STATISTICS_SHOW_SUM_LINE, true)){
            ArrayList<Long> originalSumLongs = new ArrayList<>();
            ArrayList<Entry> sumEntries = new ArrayList<>();
            for (MonitorDay day: days) {
                sumEntries.add(new Entry(i, ((float)day.getDuration())/3600000));
                originalSumLongs.add(day.getDuration());
                i++;
                maxYValue =
                        maxYValue < ((float)day.getDuration())/3600000 ?
                                ((float)day.getDuration())/3600000 : maxYValue;
            }
            data.add(sumEntries, originalSumLongs, getString(R.string.text_sum));
            data.setMaxYValue(maxYValue);
        }


        // Then for each task go through the day list to collect information for the corresponding
        // line
        for (int task: tasks){
            ArrayList<Entry> entries = new ArrayList<>();
            ArrayList<Long> originalLongs = new ArrayList<>();
            i = 0;
            for (MonitorDay day : days) {
                entries.add(new Entry(i, ((float)day.getTaskDuration(task))/3600000));
                originalLongs.add(day.getTaskDuration(task));
                i++;
            }
            data.add(entries, originalLongs, PetimoDbWrapper.getInstance().
                    getTaskById(task).getName());
        }

        return data;
    }
}
