package de.tud.nhd.petimo.model;

/**
 * Created by nhd on 29.08.17.
 */

public class MonitorTask {
    private final int id;
    private final String name;
    private final String category;
    private final int priority;

    public MonitorTask(int id, String name, String category, int priority) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.priority = priority;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public int getPriority() {
        return priority;
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
                this.name + "' category='" + this.category + "' priority='"
                + this.priority + "' />";
        return xml;
    }
}
