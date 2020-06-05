package com.example.lastmile.application.permission;

import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * author: wu
 * date: on 2018/11/30.
 * describe:检查权限的工具类
 */

public class PermissionsChecker {
    private Activity activity;
    private final int RESULT_CODE_LOCATION = 100;

    //读写文件的权限
    String[] permsLocation = {
            "android.permission.READ_EXTERNAL_STORAGE"
            , "android.permission.WRITE_EXTERNAL_STORAGE"};

    public PermissionsChecker(Activity activity) {
      this.activity=activity;
    }

    // 判断权限集合
    public boolean lacksPermissions(String... permissions) {
        for (String permission : permissions) {
            if (lacksPermission(permission)) {
                return true;
            }
        }
        return false;
    }

    // 判断是否缺少权限
    private boolean lacksPermission(String permission) {
        return ContextCompat.checkSelfPermission(activity, permission) ==
                PackageManager.PERMISSION_DENIED;
    }

    public Boolean checkPermission() {
        //true表示缺少该权限
        if (lacksPermissions(permsLocation)) {
            //是否弹出询问用户的弹窗
            ActivityCompat.requestPermissions(activity, permsLocation, RESULT_CODE_LOCATION);
        }
        //再查询一次
        if (lacksPermissions(permsLocation)) {
            return true;
        }
        return false;
    }

}