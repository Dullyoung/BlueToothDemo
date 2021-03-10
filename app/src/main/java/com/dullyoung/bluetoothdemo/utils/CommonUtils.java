package com.dullyoung.bluetoothdemo.utils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;

/**
 * @author Dullyoung
 * Created by　Dullyoung on 2021/3/10
 **/
public class CommonUtils {
    public static boolean isBlueToothSupport(Context context) {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            ToastCompat.show(context, "本机没有找到蓝牙硬件或驱动！");
            return false;
        } else {
            return true;
        }
    }
}
