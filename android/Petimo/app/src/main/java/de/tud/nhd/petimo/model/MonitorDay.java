package de.tud.nhd.petimo.model;

import java.util.List;

/**
 * Created by nhd on 28.08.17.
 */

public class MonitorDay {
    private List<MonitorBlock> monitorBlocks;
    private int date;

    public MonitorDay(int date, List<MonitorBlock> monitorBlocks){
        this.date = date;
        this.monitorBlocks = monitorBlocks;
    }
}
