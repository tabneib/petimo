package de.tud.nhd.petimo.view.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Calendar;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.view.fragments.ChartFragment;
import de.tud.nhd.petimo.view.fragments.menu.PetimoDatePickerMenu;

/**
 * TODO: customized IAxisValueFormatter for displaying monitor dates
 * TODO: Input for Chart: Update MonitorDay code | DatePickers
 * TODO: Chart Fragments - refreshable | Async Task | Modular Data Inputting
 * TODO: Overflow menu: Full Screen | Show selected tasks, Show selected categories | Line Chart, Bar Chart, Pie Chart | New Customized Report | Export As File
 */
public class StatisticsActivity extends AppCompatActivity
        implements PetimoDatePickerMenu.OnDateRangeChangeListener{

    private static final String TAG = "StatisticsActivity";
    public static final String CHART_FRAGMENT_TAG = TAG + "Chart-Fragment";
    public static final String MENU_FRAGMENT_TAG = TAG + "Menu-Fragment";

    PetimoDatePickerMenu datePickerMenu;


    private ChartFragment chartFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        datePickerMenu = PetimoDatePickerMenu.newInstance();
        getSupportFragmentManager().beginTransaction().add(
                R.id.menu_container, datePickerMenu, MENU_FRAGMENT_TAG).commit();
    }

    @Override
    public void onDateChanged(Calendar fromCalendar, Calendar toCalendar) {
        // TODO
    }
}
