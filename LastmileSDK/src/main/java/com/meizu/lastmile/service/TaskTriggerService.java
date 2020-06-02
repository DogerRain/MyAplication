package com.meizu.lastmile.service;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.meizu.lastmile.Utils.CommonUtils;
import com.meizu.lastmile.constants.ConstantUtils;
import com.meizu.lastmile.requestObj.Instruction;
import com.meizu.lastmile.requestObj.Options;
import com.meizu.statsapp.v3.PkgType;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @Author: huangyongwen
 * @CreateDate: 2020/5/20 16:56
 * @Description: 任务触发service
 */

public class TaskTriggerService {
    private String TAG = Thread.currentThread().getName() + "--->>>LastMileSDK--->>> TaskTriggerService";

    private Context context;

    public TaskTriggerService(Context context) {
        this.context = context;
    }

    public void receiveTask(final String jsonString) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                receiveTaskSynchronize(jsonString);
            }
        });
        thread.start();
    }

    private void receiveTaskSynchronize(String jsonString) {
        Instruction instruction = JSON.parseObject(jsonString, Instruction.class);
        String taskType = instruction.getTaskType();
        if (StringUtils.isBlank(taskType)){
            Log.i(TAG, "type错误");
            return;
        }
        switch (taskType) {
            case ConstantUtils.PING:
                PingService pingService = new PingService(jsonString, context);
                pingService.receiveInstructionAndStorage();
                break;
            case ConstantUtils.PAGE:
                PageAndDownloadService pageService = new PageAndDownloadService(jsonString, context);
                pageService.receiveInstructionAndStorage();
                break;
            case ConstantUtils.DOWNLOAD:
                PageAndDownloadService downloadService = new PageAndDownloadService(jsonString, context);
                downloadService.receiveInstructionAndStorage();
                break;
            default:
                break;
        }
    }


    public String getRemoteLastestTask() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("https://meizitu.baimuxym.cn/meizitu/test")
                .get().build();
        Call call = client.newCall(request);
        String lastestJsonString = "";
        try {
            Response response = call.execute();
            lastestJsonString = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lastestJsonString;
    }


    public void startTask(final String eventName, final String pageName, final PkgType pkgType, final String key, final Options options) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String lastestJsonString = getRemoteLastestTask();
                receiveTaskSynchronize(lastestJsonString);
                int flag = CommonUtils.getNetWorkStart(context);
                switch (flag) {
                    case CommonUtils.NETWORW_WIFI:
                        startPingTask(eventName, pageName, pkgType, key, options);
                        startSingleWebTask(eventName, pageName, pkgType, key, options);
                        startFileDownloadTask(eventName, pageName, pkgType, key, options);
                        break;
                    case CommonUtils.NETWORK_MOBILE:
                        startPingTask(eventName, pageName, pkgType, key, options);
                        break;
                    default:
                        break;
                }
            }
        });
        thread.start();
    }


    /**
     * 启动ping任务
     */
    private void startPingTask(String eventName, String pageName, PkgType pkgType, String key, Options options) {
        new ExcuseLocalTaskService(context, ConstantUtils.PING, ConstantUtils.T_PING, options, eventName, pageName, pkgType, key)
                .excuseLocalTask();
    }


    /**
     * 启动网页任务
     */
    private void startSingleWebTask(String eventName, String pageName, PkgType pkgType, String key, Options options) {
        new ExcuseLocalTaskService(context, ConstantUtils.PAGE, ConstantUtils.T_PAGE_DOWNLOAD, options, eventName, pageName, pkgType, key)
                .excuseLocalTask();

    }

    /**
     * 启动文件下载任务
     */
    private void startFileDownloadTask(String eventName, String pageName, PkgType pkgType, String key, Options options) {
        new ExcuseLocalTaskService(context, ConstantUtils.DOWNLOAD, ConstantUtils.T_PAGE_DOWNLOAD, options, eventName, pageName, pkgType, key)
                .excuseLocalTask();
    }

}
