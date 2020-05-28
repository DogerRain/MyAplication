package com.meizu.lastmile.service;

import android.content.Context;
import android.util.Log;

import com.meizu.lastmile.Utils.CommonUtils;
import com.meizu.lastmile.Utils.ConstantUtils;
import com.meizu.lastmile.requestObj.Options;

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

    public void startTask(Options options) {
        int flag = CommonUtils.getNetWorkStart(context);

        switch (flag) {
            case CommonUtils.NETWORW_WIFI:
                startPingTask(options);
                startSingleWebTask(options);
                startFileDownloadTask(options);
                break;
            case CommonUtils.NETWORK_MOBILE:
                startPingTask(options);
                break;
            default:
                break;
        }
    }


    /**
     * 启动ping任务
     */
    private void startPingTask(Options options) {
        new ExcuseLocalTaskService(context, ConstantUtils.PING, ConstantUtils.T_PING, options).start();
        Log.i(TAG, "启动ping任务呀");
    }


    /**
     * 启动网页任务
     */
    private void startSingleWebTask(Options options) {
        new ExcuseLocalTaskService(context, ConstantUtils.PAGE, ConstantUtils.T_PAGE_DOWNLOAD, options).start();
        Log.i(TAG, "启动网页任务");

    }

    /**
     * 启动文件下载任务
     */
    private void startFileDownloadTask(Options options) {
        new ExcuseLocalTaskService(context, ConstantUtils.DOWNLOAD, ConstantUtils.T_PAGE_DOWNLOAD, options).start();
        Log.i(TAG, "启动文件下载任务");
    }

}
