package com.meizu.lastmile.permission;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * author: wu
 * date: on 2018/11/30.
 * describe:检查权限的工具类
 */

public class PermissionsChecker {
    private final Context mContext;
    private final int RESULT_CODE_LOCATION = 0x001;
    PermissionsChecker permissionsCheckerB;
    //读写文件的权限
    String[] permsLocation = {"android.com.example.huangyongwen.myapplication.service.permission.READ_EXTERNAL_STORAGE"
            , "android.com.example.huangyongwen.myapplication.service.permission.WRITE_EXTERNAL_STORAGE"};


    public PermissionsChecker(Context context) {
        mContext = context.getApplicationContext();
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
        return ContextCompat.checkSelfPermission(mContext, permission) ==
                PackageManager.PERMISSION_DENIED;
    }

    public void check(Activity activity) {
        if (lacksPermissions(permsLocation)) {
            //是否弹出询问用户的弹窗
            ActivityCompat.requestPermissions(activity, permsLocation, RESULT_CODE_LOCATION);
        }
    }
}