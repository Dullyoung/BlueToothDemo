package com.dullyoung.bluetoothdemo.utils;

import android.os.Handler;
import android.os.Looper;

/**
 * @author Dullyoung
 * Created by　Dullyoung on 2021/3/17
 **/
public class VKit {
    private static Handler mHandler = new Handler(Looper.getMainLooper());

    public static void post(Runnable runnable) {
        mHandler.post(runnable);
    }

    public static void postDelay(long time, Runnable runnable) {
        mHandler.postDelayed(runnable, time);
    }
}
