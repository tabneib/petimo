package de.tud.nhd.petimo.model;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.tud.nhd.petimo.utils.PetimoTimeUtils;
import de.tud.nhd.petimo.controller.exception.DbErrorException;
import de.tud.nhd.petimo.controller.exception.InvalidCategoryException;
import de.tud.nhd.petimo.controller.exception.InvalidInputNameException;

/**
 * Created by nhd on 29.08.17.
 */

public class PetimoDbDemo {

    private final String TAG = "PetimoDemo";
    PetimoDbWrapper dbWrapper;

    private Calendar calendar;
    //private final String DATE_YESTERDAY = "20170829";
    private final String DATE_YESTERDAY = Integer.toString(PetimoTimeUtils.getTodayDate() - 1);

    private final String DATE_TODAY = Integer.toString(PetimoTimeUtils.getTodayDate());

    private final String[] cat1 = {"work", "10"};
    private final String[] cat2 = {"study", "5"};

    private final String[] task1 = {"kali", "work", "8"};
    private final String[] task2 = {"android", "work", "7"};
    private final String[] task3 = {"kn2", "study", "9"};
    private final String[] task4 = {"itsec", "study", "8"};

    long[] times = new long[12];



    List<String[]> cats = new ArrayList<>();
    List<String[]> tasks = new ArrayList<>();
    List<String[]> blocks = new ArrayList<>();



    public PetimoDbDemo(Context context){

        PetimoDbWrapper.setContext(context);
        this.dbWrapper = PetimoDbWrapper.getInstance();

        // Prepare calendar & time
        this.calendar = Calendar.getInstance();
        // Set date to yesterday
        Log.d(TAG, "Before: Calendar.day ===> " + calendar.get(Calendar.DAY_OF_MONTH));
        calendar.setTimeInMillis(calendar.getTimeInMillis() - 24*3600*100);
        // Set the time of calendar to 00:00 yesterday
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH) - 1, 0, 0, 0);
        Log.d(TAG, "After: Calendar.day ===> " + calendar.get(Calendar.DAY_OF_MONTH));
        long yesterdayBeginTime = calendar.getTimeInMillis();
        Log.d(TAG, "yesterdayBeginTime ===> " + yesterdayBeginTime);


        // Minutes from 00:00
        times[0] = 480;     // 8:00
        times[1] = 600;     // 10:00

        times[2] = 800;     // 13:20        600.000 ms
        times[3] = 900;     // 15:00

        times[4] = 1000;    // 16:40        1.200.000 ms
        times[5] = 1200;    // 20:00

        times[6] = 1400;    // 23:20        300.000 ms
        times[7] = 1450;    // 24:10

        times[8] = 1500;    // 25:00        300.000 ms
        times[9] = 1550;    // 25:50

        times[10] = 1800;   // 30:00
        times[11] = 1850;   // 30:50

        // Convert to ms
        for (int i = 0; i < times.length; i++) {
            Log.d(TAG, PetimoTimeUtils.getTimeFromMs(times[i]*60*1000) + " -> " +
                    ((times[i] * 60 * 1000) + yesterdayBeginTime));

            times[i] = (times[i] * 60 * 1000) + yesterdayBeginTime;

        }


        // prepare data
        String[] block1 = {"kali", "work","", "", "", DATE_YESTERDAY, "3", "0"};
        String[] block2 = {"android", "work","", "", "", DATE_YESTERDAY, "3", "0"};
        String[] block3 = {"kali", "work","", "", "", DATE_YESTERDAY, "3", "0"};
        String[] block4 = {"kn2", "study","", "", "", DATE_YESTERDAY, "3", "0"};
        String[] block5 = {"kn2", "study","", "", "", DATE_YESTERDAY, "3", "0"};
        String[] block6 = {"itsec", "study","", "", "", DATE_TODAY, "3", "0"};

        cats.add(cat1);
        cats.add(cat2);
        tasks.add(task1);
        tasks.add(task2);
        tasks.add(task3);
        tasks.add(task4);
        blocks.add(block1);
        blocks.add(block2);
        blocks.add(block3);
        blocks.add(block4);
        blocks.add(block5);
        blocks.add(block6);

        // Set the times for each block in blocks
        for (int i = 0; i < 6; i++){
            String[] block = blocks.get(i);
            blocks.remove(i);
            block[2] = Long.toString(times[i*2]);
            block[3] = Long.toString(times[i*2+1]);
            block[4] = Long.toString(times[i*2+1] - times[i*2]);
            blocks.add(i, block);
        }

        for (String[] block : blocks)
            Log.d(TAG, "added to blocks: " + block[4]);

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
                try {
                    dbWrapper.insertCategory(cat[0], Integer.parseInt(cat[1]));
                } catch (DbErrorException e) {
                    e.printStackTrace();
                } catch (InvalidInputNameException e) {
                    e.printStackTrace();
                } catch (InvalidCategoryException e) {
                    e.printStackTrace();
                }
            Log.d(TAG, "Inserting Categories: done");
            Log.d(TAG, "Inserting Tasks");
            for (String[] task: tasks)
                try {
                    dbWrapper.insertTask(task[0], task[1], Integer.parseInt(task[2]));
                } catch (DbErrorException e) {
                    e.printStackTrace();
                } catch (InvalidInputNameException e) {
                    e.printStackTrace();
                } catch (InvalidCategoryException e) {
                    e.printStackTrace();
                }
            Log.d(TAG, "Inserting Tasks: done");
            Log.d(TAG, "Inserting Monitor Blocks");
            for (String[] block: blocks)
                try {
                    dbWrapper.insertMonitorBlock(block[0], block[1], Long.parseLong(block[2]),
                            Long.parseLong(block[3]), Long.parseLong(block[4]),
                            Integer.parseInt(block[5]), Integer.parseInt(block[6]),
                            Integer.parseInt(block[7]));
                } catch (DbErrorException e) {
                    e.printStackTrace();
                } catch (InvalidCategoryException e) {
                    e.printStackTrace();
                }
            Log.d(TAG, "Inserting Monitor Blocks: done");

            Log.d(TAG, "Fetching day");
            MonitorDay mDay = dbWrapper.getDay(Integer.parseInt(DATE_YESTERDAY));
            Log.d(TAG, "Fetching day: done");
            Log.d(TAG, mDay.toXml(0));
            System.out.println("----------------------------------------------");
            dbWrapper.generateXml();

            return true;
        }
    }
}
