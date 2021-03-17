package com.dullyoung.bluetoothdemo.utils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.dullyoung.bluetoothdemo.R;

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

    public static boolean checkBleSupport(Context context) {
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            ToastCompat.show(context, "您的设备不支持BLE");
            return false;
        }
        return true;
    }
}
