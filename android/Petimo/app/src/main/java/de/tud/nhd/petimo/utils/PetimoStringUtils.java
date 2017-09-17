package de.tud.nhd.petimo.utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by nhd on 11.09.17.
 */

public class PetimoStringUtils {

    private static final String TAG = "PetimoStringUtils";
    // String structure
    public static final String ESCAPE = "%#";
    public static final String ITEM_SEPARATOR = ESCAPE + "~";
    public static final String VALUE_SEPARATOR = ESCAPE + "_";

    /**
     *
     * @param inputStr
     * @return
     * @throws StringParsingException
     */
    public static ArrayList<String[]> parse(String inputStr, int itemSize)
            throws StringParsingException {
        if (inputStr == null)
            throw new StringParsingException("Input String is null!");
        inputStr = inputStr.trim();
        if (inputStr.isEmpty())
            throw new StringParsingException("Input String is empty!");

        String[] strItems = inputStr.split(ITEM_SEPARATOR);
        ArrayList<String[]> result = new ArrayList<>();
        for (int i = 0; i < strItems.length; i++){
            String[]  strValues = strItems[i].split(VALUE_SEPARATOR);
            if (strValues.length != itemSize)
                throw new StringParsingException("Invalid item size: expected " + itemSize +
                        " but actually " + strValues.length + " - String: " +
                        Arrays.toString(strValues));
            result.add(strValues);
        }
        return result;
    }

    /**
     *
     * @return
     */
    public static String encode(ArrayList<String[]> strList, int itemSize){
        String encoded = "";
        if (strList == null || strList.isEmpty())
            throw new StringEncodingException();
        try {
            for (String[] item : strList){
                for (int i=0; i < (itemSize-1); i++)
                    encoded = encoded + item[i] + VALUE_SEPARATOR;
                // Last value of the item
                encoded = encoded + item[itemSize-1] + ITEM_SEPARATOR;
            }
            // remove the redundant ITEM_SEPARATOR at the end and return
            return encoded.substring(0, encoded.length() - ITEM_SEPARATOR.length());
        }
        catch (Exception e){
            throw new StringEncodingException();
        }
    }
}
