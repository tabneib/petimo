package de.tud.nhd.petimo.model.db;

import android.util.Log;

import java.util.ArrayList;

/**
 * This class contains information of a task or a category that is monitored on a particular day
 */

public class MonitorDayGrouped {
    private final String descriptiveName;
    private final int id;
    private ArrayList<Integer> blockIds;
    private final MonitorDay day;
    private long duration = 0;
    private int percentage = 0;

    public MonitorDayGrouped(MonitorDay day, int id, String descriptiveName, ArrayList<Integer> blockIds,
                             int duration, int percentage){
        this.day = day;
        this.id = id;
        this.descriptiveName = descriptiveName;
        this.blockIds = blockIds;
        this.duration = duration;
        this.percentage = percentage;
    }

    public MonitorDayGrouped(MonitorDay day, int id, String descriptiveName){
        this.day = day;
        this.id = id;
        this.descriptiveName = descriptiveName;
        blockIds = new ArrayList<>();
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

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage){
        this.percentage = percentage;
    }

    public void addBlock(int blockId){
        this.blockIds.add(blockId);
    }

    public void increaseDuration(long value){
        this.duration = this.duration + value;
    }

    public int getId() {
        return id;
    }
}
