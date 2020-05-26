package com.meizu.lastmile.service;

import android.content.Context;
import android.util.Log;

import com.meizu.lastmile.Utils.CommonUtils;

/**
 * @Author: huangyongwen
 * @CreateDate: 2020/5/20 16:56
 * @Description: 任务触发service
 */

public class TaskTriggerService {

    private String TAG = "LastMileSDK》》》 TaskTriggerService";

    private Context context;

    public TaskTriggerService(Context context) {
        this.context = context;
    }

    public void startTask() {
        int flag = CommonUtils.getNetWorkStart(context);

        switch (flag) {
            case CommonUtils.NETWORW_WIFI:
                startPingTask();
                startSingleWebTask();
                startFileDownloadTask();
                break;
            case CommonUtils.NETWORK_MOBILE:
                startPingTask();
                break;
            default:
                break;
        }
    }


    /**
     * 启动ping任务
     */
    private void startPingTask() {
        Log.i(TAG, "启动ping任务呀");
        PingRunLocalTaskService pingRunLocalTaskService = new PingRunLocalTaskService(context);
        pingRunLocalTaskService.start();
    }


    /**
     * 启动网页任务
     */
    private void startSingleWebTask() {
        Log.i(TAG, "启动网页任务");
    }

    /**
     * 启动文件下载任务
     */
    private void startFileDownloadTask() {
        Log.i(TAG, "启动文件下载任务");
    }

}
