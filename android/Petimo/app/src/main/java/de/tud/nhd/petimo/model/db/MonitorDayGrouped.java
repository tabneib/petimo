package de.tud.nhd.petimo.model.db;

import java.util.ArrayList;

/**
 * This class contains information of a task or a category that is monitored on a particular day
 */

public class MonitorDayGrouped {
    private String descriptiveName;
    private ArrayList<Integer> blockIds;
    private MonitorDay day;
    private long duration;
    private float percentage;

    public MonitorDayGrouped(MonitorDay day, String descriptiveName, ArrayList<Integer> blockIds,
                             int duration, float percentage){
        this.day = day;
        this.descriptiveName = descriptiveName;
        this.blockIds = blockIds;
        this.duration = duration;
        this.percentage = percentage;
    }

    public String getDescriptiveName() {
        return descriptiveName;
    }

    public ArrayList<Integer> getBlockIds() {
        return blockIds;
    }

    public MonitorDay getDay() {
        return day;
    }

    public long getDuration() {
        return duration;
    }

    public float getPercentage() {
        return percentage;
    }
}
