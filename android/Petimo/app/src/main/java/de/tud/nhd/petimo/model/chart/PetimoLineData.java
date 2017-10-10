package de.tud.nhd.petimo.model.chart;

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

    public PetimoLineData(ArrayList<Integer> dates){
        this.dates = dates;
        allLinesEtries = new ArrayList<>();
        lineLabels = new ArrayList<>();
    }

    public void add(ArrayList<Entry> lineEntries, String lineLabel){
        this.allLinesEtries.add(lineEntries);
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

    public void setMaxYValue(float maxYValue) {
        // Set maxYValue bigger by 1
        this.maxYValue = maxYValue < 3 ? 3 : (maxYValue < 24 ? maxYValue +1 : maxYValue);
    }

}
