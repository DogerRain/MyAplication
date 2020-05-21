package com.meizu.lastmile.service;

import android.content.Context;

import com.meizu.lastmile.Utils.CommonUtils;

/**
 * @Author: huangyongwen
 * @CreateDate: 2020/5/20 16:56
 * @Description: 任务触发service
 */

public class TaskTriggerService extends Thread {


    public void startTask(Context context) {
        int flag = CommonUtils.getNetWorkStart(context);

        switch (flag) {
            case CommonUtils.NETWORW_WIFI:
                startPingTask(context);
                startSingleWebTask(context);
                startFileDownloadTask(context);
                break;
            case CommonUtils.NETWORK_MOBILE:
                startPingTask(context);
                break;
            default:
                break;
        }
    }


    /**
     * 启动ping任务
     */
    private void startPingTask(Context context) {

    }


    /**
     * 启动curl任务
     */
    private void startSingleWebTask(Context context) {

    }

    /**
     * 启动curl任务
     */
    private void startFileDownloadTask(Context context) {

    }

}
