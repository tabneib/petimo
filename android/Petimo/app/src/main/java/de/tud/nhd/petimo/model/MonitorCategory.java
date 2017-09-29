package de.tud.nhd.petimo.model;

/**
 * Created by nhd on 29.08.17.
 */

public class MonitorCategory {
    // status constraints
    public static final String ACTIVE = "ACTIVE";
    public static final String DEACTIVE = "DEACTIVE";
    public static final String DELETED = "DELETED";

    // column fields
    private final int id;
    private final String name;
    private final int priority;
    private final String status;
    private final long deleteTime;
    private final String note;

    public MonitorCategory(int id, String name, int priority,
                           String status, long deleteTime, String note) {
        this.id = id;
        this.name = name;
        this.priority = priority;
        this.status = status;
        this.deleteTime = deleteTime;
        this.note = note;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
    }

    public String getStatus() {
        return status;
    }

    public long getDeleteTime() {
        return deleteTime;
    }

    public String getNote() {
        return note;
    }

    /**
     * TODO comment em
     * @return
     */
    public String toXml(int indentLevel){
        String indent = "";
        for (int i = 1; i <= indentLevel; i++)
            indent = indent + "\t";

        String xml = indent + "<MonitorCategory id='" + this.id + "' name='" +
                this.name + "' priority='" + this.priority + "' status='" +
                this.status + "' delete_time='" +
                this.deleteTime + "' note='" +
                this.note + "' />";
        return xml;
    }
}
