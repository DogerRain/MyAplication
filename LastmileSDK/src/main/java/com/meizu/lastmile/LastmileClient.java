package com.meizu.lastmile;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.meizu.lastmile.Utils.ConstantUtils;
import com.meizu.lastmile.requestObj.Instruction;
import com.meizu.lastmile.service.PingService;
import com.meizu.lastmile.service.TaskTriggerService;


/**
 * Created by huangyongwen on 2020/5/13.
 */

public class LastmileClient implements Lastmile {
    Context context;

    public LastmileClient(Context context) {
        this.context = context;
    }


    @Override
    public void reviceInstructions(String jsonString) {
        //起一个线程
        PingService pingService = new PingService(jsonString, context);
        Instruction instruction = JSON.parseObject(jsonString, Instruction.class);
        String taskType = instruction.getTaskType();
        switch (taskType) {
            case ConstantUtils.PING:
                pingService.start();
                break;
            case ConstantUtils.PAGE:
                pingService.start();
                break;
            case ConstantUtils.DOWNLOAD:
                pingService.start();
                break;
            default:
                break;
        }
    }

    @Override
    public void getLastestTask() {

    }



    @Override
    public void startLocalTask() {
        TaskTriggerService taskTriggerService = new TaskTriggerService(context);
        taskTriggerService.startTask();
    }


}
