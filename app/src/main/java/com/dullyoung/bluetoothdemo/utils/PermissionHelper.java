package com.dullyoung.bluetoothdemo.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class PermissionHelper {
    private String[] mustPermissions = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

    public void setMustPermissions(String[] mustPermissions) {
        this.mustPermissions = mustPermissions;
    }

    public void setMustPermissions2(String... mustPermissions) {
        this.mustPermissions = mustPermissions;
    }

    public PermissionHelper justStoragePermission() {
        this.mustPermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};
        return this;
    }

    public void checkAndRequestPermission(Activity activity, OnRequestPermissionsCallback onRequestPermissionsCallback) {
        this.onRequestPermissionsCallback = onRequestPermissionsCallback;

        if (Build.VERSION.SDK_INT < 23) {
            if (onRequestPermissionsCallback != null) {
                onRequestPermissionsCallback.onRequestPermissionSuccess();
            }
            return;
        }

        if (checkMustPermissions(activity)) {
            if (onRequestPermissionsCallback != null) {
                onRequestPermissionsCallback.onRequestPermissionSuccess();
            }
            return;
        }
        ActivityCompat.requestPermissions(activity, mustPermissions, 1024);
    }

    @TargetApi(23)
    public boolean checkMustPermissions(Activity activity) {
        if (mustPermissions == null || mustPermissions.length == 0) {
            return true;
        }
        List<String> lackedPermission = new ArrayList<>();
        for (String permissions : mustPermissions) {
            if (!(ContextCompat.checkSelfPermission(activity, permissions) == PackageManager.PERMISSION_GRANTED)) {
                lackedPermission.add(permissions);
            }
        }

        if (0 == lackedPermission.size()) {
            return true;
        }

        return false;
    }

    public void onRequestPermissionsResult(Activity activity, int requestCode) {
        if (!(activity instanceof Activity)) {
            return;
        }
        if (requestCode == 1024 && checkMustPermissions(activity)) {
            if (onRequestPermissionsCallback != null) {
                onRequestPermissionsCallback.onRequestPermissionSuccess();
            }
        } else {
            if (onRequestPermissionsCallback != null) {
                onRequestPermissionsCallback.onRequestPermissionError();
            }
        }
    }

    private OnRequestPermissionsCallback onRequestPermissionsCallback;

    public interface OnRequestPermissionsCallback {
        void onRequestPermissionSuccess();

        void onRequestPermissionError();
    }
}
