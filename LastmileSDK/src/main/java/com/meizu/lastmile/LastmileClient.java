package com.meizu.lastmile;

import android.content.Context;
import android.content.pm.PackageManager;
/**
 * Created by huangyongwen on 2020/5/13.
 */

public class LastmileClient implements Lastmile {

    public void getAllLastmileData() {
        System.out.println("111111");
    }

    public static boolean hasExternalStoragePermission(Context context) {
        int perm = context.checkCallingOrSelfPermission("android.com.example.huangyongwen.myapplication.service.permission.WRITE_EXTERNAL_STORAGE");
        return perm == PackageManager.PERMISSION_GRANTED;
    }



}
