package de.tud.nhd.petimo.view.fragments;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.model.chart.PetimoLineData;
import de.tud.nhd.petimo.model.sharedpref.PetimoSettingsSPref;
import de.tud.nhd.petimo.utils.PetimoTimeUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChartFragment extends Fragment
        implements OnChartValueSelectedListener {

    public ChartType chartType;
    private LineChart mLineChart;
    private ChartDataProvider mDataProvider;
    private PetimoLineData pLineData;

    public ChartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof ChartDataProvider))
            throw new RuntimeException(context.toString()
                    + " must implement ChartDataProvider");
        else
            this.mDataProvider = (ChartDataProvider) context;
    }

    /**
     * @return A new instance of fragment ChartFragment.
     */
    public static ChartFragment newInstance(ChartType chartType) {
        ChartFragment fragment = new ChartFragment();
        fragment.chartType = chartType;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chart, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mLineChart = (LineChart) view.findViewById(R.id.lineChart);
        invalidateChart(true);
    }

    /**
     *
     */
    public void invalidateChart(boolean initCall){

        //----------------------------------------------------------------------------------------->
        // Line Chart Setting
        //<-----------------------------------------------------------------------------------------

        if (initCall){
            mLineChart.setNoDataTextColor(Color.BLACK);
            mLineChart.setOnChartValueSelectedListener(this);

            // no description text
            mLineChart.getDescription().setEnabled(false);

            // enable touch gestures
            mLineChart.setTouchEnabled(true);

            mLineChart.setDragDecelerationFrictionCoef(0.9f);

            // enable scaling and dragging
            mLineChart.setDragEnabled(true);
            mLineChart.setScaleEnabled(true);
            mLineChart.setDrawGridBackground(false);
            //mLineChart.setHighlightPerDragEnabled(true);

            // if disabled, scaling can be done on x- and y-axis separately
            mLineChart.setPinchZoom(PetimoSettingsSPref.getInstance().getBoolean(
                    PetimoSettingsSPref.STATISTICS_ENABLE_PINCH_ZOOM, false));

            // set an alternative background color
            mLineChart.setBackgroundColor(Color.WHITE);
            //mLineChart.setBackgroundColor(Color.WHITE);

            // TODO throw to a const
            mLineChart.animateX(1500);

        }

        // add data
        mLineChart.setData(this.getLineData());


        //------------- Legend -------------------------------------------------------------------->

        // get the legend (only possible after setting data)
        Legend l = mLineChart.getLegend();

        if (initCall){
            l.mNeededWidth = mLineChart.getWidth();
            // modify the legend ...
            l.setForm(Legend.LegendForm.SQUARE);
            // TODO modularize
            l.setTextSize(11f);
            l.setTextColor(Color.BLACK);
            l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
            l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            l.setDrawInside(false);
            //
            l.setWordWrapEnabled(true);
            // l.setYOffset(11f);
        }


        //------------- X-Axis -------------------------------------------------------------------->

        if (initCall){
            XAxis xAxis = mLineChart.getXAxis();
            // TODO modularize
            xAxis.setTextSize(11f);
            xAxis.setTextColor(Color.BLACK);
            xAxis.setDrawGridLines(false);
            xAxis.setDrawAxisLine(true);
            xAxis.setGranularity(1);
            xAxis.setValueFormatter(new DayAxisValueFormatter(mLineChart));
        }


        //------------- Y-Axises -------------------------------------------------------------------->

        YAxis leftAxis = mLineChart.getAxisLeft();
        YAxis rightAxis = mLineChart.getAxisRight();

        if (initCall){
            leftAxis.setTextColor(ColorTemplate.getHoloBlue());
            leftAxis.setTextSize(13f);
            leftAxis.setAxisMinimum(0);
            leftAxis.setDrawGridLines(true);
            leftAxis.setGranularityEnabled(false);
            leftAxis.setGranularity(1);

            //rightAxis.setTextColor(Color.RED);
            rightAxis.setTextColor(ColorTemplate.getHoloBlue());
            rightAxis.setTextSize(13f);
            rightAxis.setAxisMinimum(0);
            rightAxis.setDrawGridLines(false);
            rightAxis.setDrawZeroLine(false);
            rightAxis.setGranularityEnabled(false);
            rightAxis.setGranularity(1);
        }

        // This is adapted to data each time data is changed
        leftAxis.setAxisMaximum(pLineData.getMaxYValue());
        rightAxis.setAxisMaximum(pLineData.getMaxYValue());

        if (!initCall) {
            mLineChart.removeAllViews();
            mLineChart.invalidate();
        }
    }

    private LineData getLineData(){

        pLineData = this.mDataProvider.getData();
        ArrayList<ILineDataSet> lineDataSet = new ArrayList<>();

        //TODO: hard-code for now, delete it later
        int[] colors = new int[]{ColorTemplate.getHoloBlue(),
                Color.RED, Color.YELLOW};

        for (int i=0; i < pLineData.getDataSize(); i++){
            LineDataSet aSet = new LineDataSet(pLineData.getLineEntries(i), pLineData.getLabel(i));

            // TODO: adapt (vary it)
            // TODO: Figure out if only the first line can depend on LEFT, all further -> RIGHT ?
            aSet.setAxisDependency(YAxis.AxisDependency.RIGHT);

            // Equal for all lines
            aSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
            aSet.setDrawFilled(false);
            aSet.setCircleColor(Color.BLACK);
            aSet.setLineWidth(2f);
            aSet.setCircleRadius(3f);
            aSet.setFillAlpha(65);
            aSet.setHighLightColor(Color.rgb(244, 117, 117));
            aSet.setDrawCircleHole(false);

            // TODO: vary it !
            aSet.setColor(colors[i % colors.length]);
            aSet.setFillColor(ColorTemplate.getHoloBlue());

            // Set the valueFormatter to display time in HH:MM format
            aSet.setValueFormatter(new TimeValueFormatter(i));

            //aSet.setFillFormatter(new MyFillFormatter(0f));
            //aSet.setDrawHorizontalHighlightIndicator(false);
            //aSet.setVisible(false);
            //aSet.setCircleHoleColor(Color.WHITE);
            lineDataSet.add(aSet);
        }

        // create a data object with the datasets
        LineData data = new LineData(lineDataSet);

        // x-axis text value text color
        data.setValueTextColor(Color.BLACK);
        data.setValueTextSize(11f);

        return data;
    }

    public void updateSettings(String settings){
        switch (settings){
            case ChartSettings.PINCH_ZOOM:
                mLineChart.setPinchZoom(PetimoSettingsSPref.getInstance().getBoolean(
                        PetimoSettingsSPref.STATISTICS_ENABLE_PINCH_ZOOM, false));
                break;
            default:
                throw new RuntimeException("Unknown Chart Settings: " + settings);
        }
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        mLineChart.centerViewToAnimated(
                e.getX(), e.getY(), mLineChart.getData().getDataSetByIndex(h.getDataSetIndex())
                .getAxisDependency(), 500);
    }

    @Override
    public void onNothingSelected() {

    }


    public enum ChartType{
        LINE_CHART,
        BAR_CHART,
        PIE_CHART
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow the fragment to get data for rendering the chart
     */
    public interface ChartDataProvider{
        public PetimoLineData getData();
    }


    /**
     *
     */
    private class DayAxisValueFormatter implements IAxisValueFormatter {

        private BarLineChartBase<?> chart;

        public DayAxisValueFormatter(BarLineChartBase<?> chart) {
            this.chart = chart;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {

            // This try catch is a hack to overcome some unknown error occurring when the user
            // choose a smaller date range. This error causes an IndexOutOfBound to be thrown.
            // TODO figure out if this is a bug of the lib or Petimo
            try{
                int date = pLineData.getDate((int)value);

                if (chart.getVisibleXRange() > 30 * 6) {

                    return PetimoTimeUtils.getDescriptiveMonth(date) + " " +
                            PetimoTimeUtils.getDescriptiveYear(date);
                }
                else
                    return PetimoTimeUtils.getDescriptiveDay(date) + " " +
                            PetimoTimeUtils.getDescriptiveMonth(date);
            }
            catch (IndexOutOfBoundsException e){
                e.printStackTrace();
                //return "wtf ??";
                return null;
            }

        }
    }

    /**
     *
     */
    private class TimeValueFormatter implements IValueFormatter {

        private int linePosition;
        private ArrayList<Long> durations;

        public TimeValueFormatter(int linePosition){
            this.linePosition = linePosition;
            this.durations = pLineData.getOriginalDurations(linePosition);
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex,
                                        ViewPortHandler viewPortHandler) {
            String durationStr =
                    PetimoTimeUtils.getTimeFromMs(this.durations.get((int)entry.getX()));
            // We don't want to spam the graph with tons of "0:00" :)
            // We don't directly check duration == 0 because there are cases in which duration != 0
            // but durationStr is still 0:00 ;)
            if (durationStr.equals("0:00"))
                return "";
            else
                return durationStr;
        }
    }

    public static final class ChartSettings{
        public static final String PINCH_ZOOM = "PINCH_ZOOM";
    }
}
