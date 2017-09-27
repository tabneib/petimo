package de.tud.nhd.petimo.model;

import android.util.Log;

import java.util.List;

import de.tud.nhd.petimo.utils.PetimoTimeUtils;

/**
 * Created by nhd on 28.08.17.
 */

public class MonitorDay {
    public static final String TAG = "MonitorDay";
    private List<MonitorBlock> monitorBlocks;
    private int date;

    public MonitorDay(int date, List<MonitorBlock> monitorBlocks){
        this.date = date;
        this.monitorBlocks = monitorBlocks;
    }

    public List<MonitorBlock> getMonitorBlocks() {
        return monitorBlocks;
    }

    /**
     * Remove the a block from this day
     * @param position the position in the block list to be deleted
     * @return true if removed, false if nothing is remove
     */
    //unused
    public boolean removeBlock(int position){
        //Log.d(TAG, "Trying to remove the Block at position ====> " + position);
        if (monitorBlocks == null || monitorBlocks.isEmpty()
                || monitorBlocks.size() < position + 1)
            return false;
        monitorBlocks.remove(position);
        return true;
    }
    public int getDate() {
        return date;
    }

    /**
     * Calculate the total duration of all monitored blocks of this day
     * @return the total duration
     */
    public int getDuration(){
        int duration = 0;
        if (!monitorBlocks.isEmpty())
            for (MonitorBlock block : monitorBlocks)
                duration += block.getDuration();
        return duration;
    }
    /**
     * Information about this day as a string
     * @return the info string
     */
    public String getInfo(){
        String info = "Total Duration: " + PetimoTimeUtils.getTimeFromMs(getDuration());
        return info;
    }
    /**
     * TODO: comment em
     * @return
     */
    public String toXml(int indentLevel){
        String indent = "";
        for (int i = 1; i <= indentLevel; i++)
            indent = indent + "\t";

        String xml = indent + "<MonitorDay date='" + this.date + "'>\n";
        for (MonitorBlock block : this.monitorBlocks){
            xml = xml + indent + "\t"  + block.toXml(0) + "\n";
        }
        xml = xml + "</MonitorDay>";
        //System.out.println(xml);
        return xml;
    }

}
