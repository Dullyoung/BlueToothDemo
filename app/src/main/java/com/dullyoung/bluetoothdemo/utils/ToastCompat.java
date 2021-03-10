package com.dullyoung.bluetoothdemo.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * @author Dullyoung
 * Created by　Dullyoung on 2021/3/10
 **/
public class ToastCompat {
    public static void show(Context context, String string) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
    }
}
