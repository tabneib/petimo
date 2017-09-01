package de.tud.nhd.petimo.model;

/**
 * Created by nhd on 28.08.17.
 */

public class MonitorBlock {

    private int id;
    private String task;
    private String category;
    private long start;
    private long end;
    private long duration;
    private int date;
    private int weekDay;
    private boolean overNight;

    public MonitorBlock(int id, String task, String category, long start, long end, long duration,
                        int date, int weekDay, int overNight) {
        this.id = id;
        this.task = task;
        this.category = category;
        this.start = start;
        this.end = end;
        this.duration = duration;
        this.date = date;
        this.weekDay = weekDay;
        this.overNight = overNight == 0 ? false : true;
    }

    public int getId() {
        return id;
    }

    public String getTask() {
        return task;
    }

    public String getCategory() {
        return category;
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

    /**
     * TODO comment em
     * @return
     */
    public String toXml(int indentLevel){
        String indent = "";
        for (int i = 1; i <= indentLevel; i++)
            indent = indent + "\t";

        String xml = indent + "<MonitorBlock id='" + this.id + "' task='" + this.task + "' category='" +
                this.category + "' start='" + this.start + "' end='" + this.end + "' duration='" +
                this.duration + "' date='" + this.date + "' weekday='" +
                this.weekDay + "' overnight='" + this.overNight+ "' />";
        return xml;
    }
}
