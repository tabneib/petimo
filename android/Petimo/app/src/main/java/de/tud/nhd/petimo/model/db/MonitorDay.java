package de.tud.nhd.petimo.model.db;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.tud.nhd.petimo.utils.PetimoTimeUtils;

/**
 * Created by nhd on 28.08.17.
 */

public class MonitorDay {
    public static final String TAG = "MonitorDay";
    private List<MonitorBlock> monitorBlocks;
    private int date;

    ArrayList<Integer> foundTasks = null;
    ArrayList<Integer> foundCats = null;
    ArrayList<MonitorDayGrouped> groupedByTask = null;
    ArrayList<MonitorDayGrouped> groupedByCat = null;

    public MonitorDay(int date, List<MonitorBlock> monitorBlocks){
        this.date = date;
        this.monitorBlocks = monitorBlocks;
        // TODO: Make a list of

    }

    /**
     * Compute the total duration of the given task
     * @param taskId
     * @return
     */
    public long getTaskDuration(int taskId){
        ArrayList<MonitorDayGrouped> groups = getGroupedByTask();
        if (!foundTasks.contains(taskId))
            return 0;
        else
            for (MonitorDayGrouped group : groups)
                if (group.getId() == taskId)
                    return group.getDuration();
        return 0;
    }


    /**
     *
     * @return
     */
    public ArrayList<MonitorDayGrouped> getGroupedByTask() {

        if (this.groupedByTask == null){
            this.foundTasks = new ArrayList<>();
            this.groupedByTask = new ArrayList<>();
            HashMap<String, MonitorDayGrouped> found = new HashMap<>();

            for (MonitorBlock block: monitorBlocks){
                // check if the corresponding task is already found
                String descrName;
                try {
                    descrName = PetimoDbWrapper.getInstance().
                            getTaskById(block.getTaskId()).getDescriptiveName();
                }
                catch (NullPointerException e){
                    // Somehow the task with the given taskId cannot be found, just skip it
                    continue;
                }

                if (found.containsKey(descrName)){
                    found.get(descrName).addBlock(block.getId());
                    found.get(descrName).increaseDuration(block.getDuration());
                }
                else{
                    foundTasks.add(block.getTaskId());
                    found.put(descrName, new MonitorDayGrouped(this, block.getTaskId(), descrName));
                    found.get(descrName).addBlock(block.getId());
                    found.get(descrName).increaseDuration(block.getDuration());
                }
            }
            // this is a hack to workaround the "100% percentage sum" issue
            int percentageSum = 0;
            Iterator iterator = found.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry pair = (Map.Entry) iterator.next();
                MonitorDayGrouped aGroup = (MonitorDayGrouped) pair.getValue();
                aGroup.setPercentage((int)
                        (aGroup.getDuration() *100 / this.getDuration()));
                percentageSum = percentageSum + aGroup.getPercentage();
                if (!iterator.hasNext() && percentageSum < 100)
                    aGroup.setPercentage(aGroup.getPercentage() + 100 - percentageSum);
                //aGroup.setPercentage(aGroup.getDuration() * 100 / this.getDuration());
                this.groupedByTask.add(aGroup);
            }
        }
        return this.groupedByTask;
    }

    /**
     *
     * @return
     */
    public ArrayList<MonitorDayGrouped> getGroupedByCat() {
        if(this.groupedByCat == null){
            this.foundCats = new ArrayList<>();
            this.groupedByCat = new ArrayList<>();
            HashMap<String, MonitorDayGrouped> found = new HashMap<>();

            for (MonitorBlock block: monitorBlocks){
                // check if the corresponding task is already found
                String descrName = PetimoDbWrapper.getInstance().
                        getCatById(block.getCatId()).getDescriptiveName();
                if (found.containsKey(descrName)){
                    found.get(descrName).addBlock(block.getId());
                    found.get(descrName).increaseDuration(block.getDuration());
                }
                else{
                    foundCats.add(block.getCatId());
                    found.put(descrName, new MonitorDayGrouped(this, block.getCatId(), descrName));
                    found.get(descrName).addBlock(block.getId());
                    found.get(descrName).increaseDuration(block.getDuration());
                }
            }

            Iterator iterator = found.entrySet().iterator();
            // this is a hack to workaround the "100% percentage sum" issue
            int percentageSum = 0;
            while (iterator.hasNext()){
                Map.Entry pair = (Map.Entry) iterator.next();
                MonitorDayGrouped aGroup = (MonitorDayGrouped) pair.getValue();
                aGroup.setPercentage((int)
                        (aGroup.getDuration() * 100 / this.getDuration()));
                percentageSum = percentageSum + aGroup.getPercentage();
                if (!iterator.hasNext() && percentageSum < 100)
                    aGroup.setPercentage(aGroup.getPercentage() + 100 - percentageSum);
                this.getGroupedByCat().add(aGroup);
            }
        }
        return this.groupedByCat;
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
    public long getDuration(){
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
    public String getDurationStr(){
        return PetimoTimeUtils.getTimeFromMs(getDuration());
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