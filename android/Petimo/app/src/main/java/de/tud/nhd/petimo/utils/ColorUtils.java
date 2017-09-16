package de.tud.nhd.petimo.utils;

import android.graphics.Color;

/**
 * Created by nhd on 16.09.17.
 */

public class ColorUtils {

    public static boolean isDarkColor(int color){
        double darkness = 1-
                (0.299* Color.red(color) + 0.587*Color.green(color) + 0.114*Color.blue(color))/255;
        if(darkness<0.5){
            return false; // light color
        }else{
            return true; // dark color
        }
    }
}
