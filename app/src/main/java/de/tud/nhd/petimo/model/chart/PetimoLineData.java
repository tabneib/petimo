package de.tud.nhd.petimo.model.chart;

import android.util.Log;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;

import de.tud.nhd.petimo.utils.PetimoTimeUtils;

/**
 * Created by nhd on 06.10.17.
 */

public class PetimoLineData {

    private ArrayList<ArrayList<Entry>> allLinesEtries;
    private ArrayList<String> lineLabels;
    private ArrayList<Integer> dates;
    private float maxYValue = 24;
    private ArrayList<ArrayList<Long>> durations;

    public PetimoLineData(ArrayList<Integer> dates){
        this.dates = dates;
        allLinesEtries = new ArrayList<>();
        lineLabels = new ArrayList<>();
        durations = new ArrayList<>();
    }

    public void add(
            ArrayList<Entry> lineEntries, final ArrayList<Long> originalDurations, String lineLabel){
        this.allLinesEtries.add(lineEntries);
        this.durations.add(originalDurations);
        this.lineLabels.add(lineLabel);
    }

    public int getDataSize(){
        return allLinesEtries.size();
    }

    public String getLabel(int position){
        return lineLabels.get(position);
    }

    public ArrayList<Entry> getLineEntries(int position){
        return allLinesEtries.get(position);
    }

    public ArrayList<Integer> getDates() {
        return dates;
    }

    public int getDate(int position){
        return dates.get(position);
    }

    public float getMaxYValue() {
        return maxYValue;
    }

    public ArrayList<Long> getOriginalDurations(int linePosition){
        return this.durations.get(linePosition);
    }

    public void setMaxYValue(float maxYValue) {
        // Set maxYValue bigger by 1
        //this.maxYValue = maxYValue < 3 ? 6 : (maxYValue < (25 - 6) ? maxYValue + 6 : maxYValue);
        this.maxYValue = 24;
    }

}
