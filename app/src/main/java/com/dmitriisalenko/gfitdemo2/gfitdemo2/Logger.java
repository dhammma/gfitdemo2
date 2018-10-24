package com.dmitriisalenko.gfitdemo2.gfitdemo2;

import android.util.Log;

public class Logger {
    public static String TAG = "BLA BLA";

    public static void log(String message) {
        Log.v(TAG, TAG + " " + message);
    }
}
