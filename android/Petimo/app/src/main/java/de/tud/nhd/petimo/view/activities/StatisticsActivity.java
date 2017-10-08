package de.tud.nhd.petimo.view.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.Calendar;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.model.chart.PetimoLineData;
import de.tud.nhd.petimo.utils.PetimoTimeUtils;
import de.tud.nhd.petimo.view.fragments.ChartFragment;
import de.tud.nhd.petimo.view.fragments.menu.PetimoDatePickerMenu;

/**
 * TODO: customized IAxisValueFormatter for displaying monitor dates
 * TODO: Input for Chart: Update MonitorDay code | DatePickers
 * TODO: Chart Fragments - refreshable | Async Task | Modular Data Inputting
 * TODO: Overflow menu: Full Screen | Show selected tasks, Show selected categories | Line Chart, Bar Chart, Pie Chart | New Customized Report | Export As File
 */
public class StatisticsActivity extends AppCompatActivity
        implements PetimoDatePickerMenu.OnDateRangeChangeListener,
                    ChartFragment.ChartDataProvider{

    private static final String TAG = "StatisticsActivity";
    public static final String LINE_CHART_FRAGMENT_TAG = TAG + "LineChart-Fragment";
    public static final String MENU_FRAGMENT_TAG = TAG + "Menu-Fragment";

    PetimoDatePickerMenu datePickerMenu;
    private ChartFragment lineChartFragment;

    private Calendar toCalendar = Calendar.getInstance();
    private Calendar fromCalendar;
    private final int DEFAULT_DATE_RANGE = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        fromCalendar = Calendar.getInstance();
        fromCalendar.add(Calendar.DATE, -1 * (DEFAULT_DATE_RANGE - 1));

        datePickerMenu = PetimoDatePickerMenu.newInstance();
        getSupportFragmentManager().beginTransaction().add(
                R.id.menu_container, datePickerMenu, MENU_FRAGMENT_TAG).commit();

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
        // TODO


        ArrayList<Integer> dates = PetimoTimeUtils.getDateIntFromRange(fromCalendar, toCalendar);
        PetimoLineData data = new PetimoLineData(dates);





        int count = 10;
        int range = 100;

        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {
            float mult = range / 2f;
            float val = (float) (Math.random() * mult) + 50;
            yVals1.add(new Entry(i, val, "foo"));
        }

        ArrayList<Entry> yVals2 = new ArrayList<Entry>();

        for (int i = 0; i < count-1; i++) {
            float mult = range;
            float val = (float) (Math.random() * mult) + 450;
            yVals2.add(new Entry(i, val));
//            if(i == 10) {
//                yVals2.add(new Entry(i, val + 50));
//            }
        }

        ArrayList<Entry> yVals3 = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {
            float mult = range;
            float val = (float) (Math.random() * mult) + 500;
            yVals3.add(new Entry(i, val));
        }


        data.add(yVals1, "foo");
        data.add(yVals2, "bar");
        data.add(yVals3, "tabneib");
        return data;
    }
}
