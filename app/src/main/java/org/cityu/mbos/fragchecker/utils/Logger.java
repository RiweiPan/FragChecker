package org.cityu.mbos.fragchecker.utils;

import android.util.Log;

/**
 * Created by Hubery on 2017/6/7.
 */

public class Logger {

    private static final String TAG = "org.cityu.os.ext4aging";

    public static void info(String info){
        Log.i(TAG,info);
    }

    public static void debug(String info){
        Log.d(TAG,info);
    }

    public static void warn(String info){
        Log.w(TAG,info);
    }

    public static void error(String info){
        Log.e(TAG,info);
    }

}
