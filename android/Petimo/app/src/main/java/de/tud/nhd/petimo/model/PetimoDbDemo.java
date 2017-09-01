package de.tud.nhd.petimo.model;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nhd on 29.08.17.
 */

public class PetimoDbDemo {

    private final String TAG = "PetimoDemo";
    PetimoDbWrapper dbWrapper;

    private final String DATE = "20170829";
    private final String[] cat1 = {"work", "10"};
    private final String[] cat2 = {"study", "5"};

    private final String[] task1 = {"kali", "work", "8"};
    private final String[] task2 = {"android", "work", "7"};
    private final String[] task3 = {"kn2", "study", "9"};
    private final String[] task4 = {"itsec", "study", "8"};

    private final String[] block1 = {"kali", "work","480", "600", "120", DATE, "3", "0"};
    private final String[] block2 = {"android", "work","600", "800", "200", DATE, "3", "0"};
    private final String[] block3 = {"kali", "work","900", "1000", "100", DATE, "3", "0"};
    private final String[] block4 = {"kn2", "study","1200", "1400", "200", DATE, "3", "0"};
    private final String[] block5 = {"kn2", "study","1450", "1500", "50", DATE, "3", "0"};
    private final String[] block6 = {"itsec", "study","1550", "1800", "250", DATE, "3", "0"};

    List<String[]> cats = new ArrayList<>();
    List<String[]> tasks = new ArrayList<>();
    List<String[]> blocks = new ArrayList<>();



    public PetimoDbDemo(Context context){
        try {
            PetimoDbWrapper.initialize(context);
            this.dbWrapper = PetimoDbWrapper.getInstance();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        // prepare data
        cats.add(cat1);
        cats.add(cat2);
        tasks.add(task1);
        tasks.add(task2);
        blocks.add(block1);
        blocks.add(block2);
        blocks.add(block3);
        blocks.add(block4);
        blocks.add(block5);
        blocks.add(block6);

    }

    /**
     * Init the example database and run the basic features
     */
    public boolean execute(){
        if (!dbWrapper.isReady()){
            Log.d(TAG,"Database wrapper is not yet ready! Please try again");
            return false;
        }
        else{
            Log.d(TAG, "Dropping tables");
            dbWrapper.dropAll();
            Log.d(TAG, "Dropping tables: done");
            Log.d(TAG, "Creating tables");
            dbWrapper.createAll();
            Log.d(TAG, "Creating tables: done");
            Log.d(TAG, "Inserting Categories");
            for (String[] cat: cats)
                dbWrapper.insertCategory(cat[0], Integer.parseInt(cat[1]));
            Log.d(TAG, "Inserting Categories: done");
            Log.d(TAG, "Inserting Tasks");
            for (String[] task: tasks)
                dbWrapper.insertTask(task[0], task[1], Integer.parseInt(task[2]));
            Log.d(TAG, "Inserting Tasks: done");
            Log.d(TAG, "Inserting Monitor Blocks");
            for (String[] block: blocks)
                dbWrapper.insertMonitorBlock(block[0], block[1], Integer.parseInt(block[2]),
                        Integer.parseInt(block[3]), Integer.parseInt(block[4]),
                        Integer.parseInt(block[5]), Integer.parseInt(block[6]),
                        Integer.parseInt(block[7]));
            Log.d(TAG, "Inserting Monitor Blocks: done");

            Log.d(TAG, "Fetching day");
            MonitorDay mDay = dbWrapper.getDay(Integer.parseInt(DATE));
            Log.d(TAG, "Fetching day: done");
            System.out.println(mDay.toXml(0));
            System.out.println("----------------------------------------------");
            dbWrapper.generateXml();


            return true;
        }


    }
}
