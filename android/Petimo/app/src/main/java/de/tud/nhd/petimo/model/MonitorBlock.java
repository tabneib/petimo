package de.tud.nhd.petimo.model;

/**
 * Created by nhd on 28.08.17.
 */

public class MonitorBlock {

    private int id;
    private String task;
    private String category;
    private int start;
    private int end;
    private int duration;
    private int date;
    private int weekDay;
    private boolean overNight;

    public MonitorBlock(int id, String task, String category, int start, int end, int duration,
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

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int getDuration() {
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
}
