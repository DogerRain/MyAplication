package com.meizu.lastmile.service;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.meizu.lastmile.Utils.ConstantUtils;
import com.meizu.lastmile.requestObj.Instruction;
import com.meizu.lastmile.requestObj.Options;

/**
 * @Author: huangyongwen
 * @CreateDate: 2020/5/26 15:12
 * @Description:
 */

public class ReceiveTaskService {

    private Context context;
    private String jsonString;

    public ReceiveTaskService(Context context, String jsonString) {
        this.context = context;
        this.jsonString = jsonString;
    }

    public void receiveInstructions() {
        //起一个线程
        Instruction instruction = JSON.parseObject(jsonString, Instruction.class);
        String taskType = instruction.getTaskType();
        switch (taskType) {
            case ConstantUtils.PING:
                PingService pingService = new PingService(jsonString, context);
                pingService.start();
                break;
            case ConstantUtils.PAGE:
                PageAndDownloadService pageService = new PageAndDownloadService(jsonString, context);
                pageService.start();
                break;
            case ConstantUtils.DOWNLOAD:
                PageAndDownloadService downloadService = new PageAndDownloadService(jsonString, context);
                downloadService.start();
                break;
            default:
                break;
        }
    }
}
