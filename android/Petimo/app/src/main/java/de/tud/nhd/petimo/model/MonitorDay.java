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

    public List<MonitorBlock> getMonitorBlocks() {
        return monitorBlocks;
    }

    public int getDate() {
        return date;
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
