package de.tud.nhd.petimo.model.db;

import de.tud.nhd.petimo.utils.PetimoTimeUtils;

/**
 * Created by nhd on 28.08.17.
 */

public class MonitorBlock {

    // status constants
    public static final String ACTIVE = "ACTIVE";
    public static final String DEACTIVE = "DEACTIVE";

    // column fields
    private final int id;
    private final long start;
    private final long end;
    private final long duration;
    private final int date;
    private final int weekDay;
    private final boolean overNight;
    private final int ovThreshold;
    private final String status;
    private final String note;
    private final int taskId;
    private final int catId;
    private final String taskName;
    private final String catName;

    MonitorBlock(int id, String taskName, int taskId, String catName, int catId, long start,
                        long end, long duration, int date, int weekDay, int overNight,
                        int ovThreshold, String status, String note) {
        this.id = id;
        this.taskId = taskId;
        this.catId = catId;
        this.taskName = taskName;
        this.catName = catName;
        this.start = start;
        this.end = end;
        this.duration = duration;
        this.date = date;
        this.weekDay = weekDay;
        this.overNight = overNight == 0 ? false : true;
        this.ovThreshold = ovThreshold;
        this.status = status;
        this.note = note;
    }

    public int getId() {
        return id;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public long getDuration() {
        return duration;
    }

    public int getDate() {
        return date;
    }

    public int getWeekDay() {
        return weekDay;
    }

    public boolean getOverNight() {
        return overNight;
    }

    public boolean isOverNight() {
        return overNight;
    }

    public int getOvThreshold() {
        return ovThreshold;
    }

    public String getStatus() {
        return status;
    }

    public String getNote() {
        return note;
    }

    public int getTaskId() {
        return taskId;
    }

    public int getCatId() {
        return catId;
    }

    public String getCatName(){
        return this.catName;
    }

    public String getTaskName(){
        return this.taskName;
    }
    /**
     * TODO: get Task/Cat name by ID!
     * @return
     */
    public String toXml(int indentLevel){
        String indent = "";
        for (int i = 1; i <= indentLevel; i++)
            indent = indent + "\t";

        String xml = indent + "<MonitorBlock id='" + this.id + "' task='" + taskName +
                "' category='" + catName + "' start='" + "' task_id='" + taskId +
                "' category_id='" + catId + "' start='" +
                PetimoTimeUtils.getDayTimeFromMsTime(this.start) + "' end='" +
                PetimoTimeUtils.getDayTimeFromMsTime(this.end) + "' duration='" +
                PetimoTimeUtils.getTimeFromMs(this.duration) + "' date='" + this.date +
                "' weekday='" +
                this.weekDay + "' overnight='" + this.overNight+ "' />";
        return xml;
    }
}
