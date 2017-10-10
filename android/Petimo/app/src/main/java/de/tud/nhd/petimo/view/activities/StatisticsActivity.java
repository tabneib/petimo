package de.tud.nhd.petimo.view.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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
import de.tud.nhd.petimo.view.fragments.menu.PetimoStatisticsMenu;

/**
 * TODO: customized IAxisValueFormatter for displaying monitor dates
 * TODO: Input for Chart: Update MonitorDay code | DatePickers
 * TODO: Chart Fragments - refreshable | Async Task | Modular Data Inputting
 * TODO: Overflow menu: Full Screen | Show selected tasks, Show selected categories | Line Chart, Bar Chart, Pie Chart | New Customized Report | Export As File
 */
public class StatisticsActivity extends AppCompatActivity
        implements PetimoStatisticsMenu.OnDateRangeChangeListener,
                    ChartFragment.ChartDataProvider{

    private static final String TAG = "StatisticsActivity";
    public static final String LINE_CHART_FRAGMENT_TAG = TAG + "LineChart-Fragment";
    public static final String MENU_FRAGMENT_TAG = TAG + "Menu-Fragment";

    private ChartFragment lineChartFragment;

    private Calendar toCalendar = Calendar.getInstance();
    private Calendar fromCalendar = Calendar.getInstance();
    private final int DEFAULT_DATE_RANGE = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);


        fromCalendar.setTime(new Date());
        toCalendar.setTime(new Date());
        fromCalendar.add(Calendar.DATE, -1 * (DEFAULT_DATE_RANGE - 1));

        getSupportFragmentManager().beginTransaction().add(
                R.id.menu_container, PetimoStatisticsMenu.newInstance(false),
                MENU_FRAGMENT_TAG).commit();

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

    @Override
    public void onDateChanged(Calendar fromCalendar, Calendar toCalendar) {
        this.fromCalendar = fromCalendar;
        this.toCalendar = toCalendar;
        // TODO update chart
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
                        PetimoSettingsSPref.getInstance().getSettingsBoolean(
                                PetimoSettingsSPref.STATISTICS_SHOW_SELECTED_TASKS, false));

        ArrayList<Integer> tasks;
        if (PetimoSettingsSPref.getInstance().getSettingsBoolean(
                PetimoSettingsSPref.STATISTICS_SHOW_SELECTED_TASKS, false))
            tasks = TaskSelector.getInstance().getSelectedTasks(PetimoSPref.Consts.STATISTICS);
        else
            tasks = PetimoDbWrapper.getInstance().getAllTaskIds();

        // First, add the sum line
        ArrayList<Entry> sumEntries = new ArrayList<>();
        int i = 0;
        for (MonitorDay day: days) {
            sumEntries.add(new Entry(i, day.getDuration()/3600000));
            i++;
            maxYValue =
                    maxYValue < day.getDuration()/3600000 ? day.getDuration()/3600000 : maxYValue;
        }
        data.add(sumEntries, getString(R.string.text_sum));
        data.setMaxYValue(maxYValue);

        // Then for each task go through the day list to collect information for the corresponding
        // line
        for (int task: tasks){
            ArrayList<Entry> entries = new ArrayList<>();
            i = 0;
            for (MonitorDay day : days) {
                entries.add(new Entry(i, day.getTaskDuration(task)/3600000));
                i++;
            }
            data.add(entries, PetimoDbWrapper.getInstance().
                    getTaskById(task).getName());
        }

        return data;
    }
}
