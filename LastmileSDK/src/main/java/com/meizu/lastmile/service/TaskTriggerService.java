package com.meizu.lastmile.service;

import android.content.Context;

import com.meizu.lastmile.Utils.CommonUtils;

/**
 * @Author: huangyongwen
 * @CreateDate: 2020/5/20 16:56
 * @Description: 任务触发service
 */

public class TaskTriggerService {

    public void startPingTask(Context context) {

        //1.判断是否处于WIFI
        if (CommonUtils.WifiConnected(context)) {
            startPingTask();
        } else {
            // 判断是否处于网络可用
            if (CommonUtils.NetworkConnected(context)) {

            }

        }

        //3. 判断是否有任务
    }

    private void startPingTask(){

    }
}
