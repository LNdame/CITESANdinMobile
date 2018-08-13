package com.batsamayi.ndincedetu;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class PermissionHandler {
    private Activity activity;

    PermissionHandler(Activity activity) {
        this.activity = activity;
    }

    public boolean needsPermission(String permission) {
        return ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission(int request, String... permission) {
        ActivityCompat.requestPermissions(activity, permission, request);
    }
}
