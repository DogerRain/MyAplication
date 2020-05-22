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

    LastmileClient(Context context) {
        this.context = context;
    }

    @Override
    public void reviceInstructions(String jsonString) {
        Instruction instruction = JSON.parseObject(jsonString, Instruction.class);
        String taskType = instruction.getTaskType();
        switch (taskType) {
            case ConstantUtils.PING:
                new PingService().receiveInstructionAndStorage(jsonString, context);
                break;
            case ConstantUtils.PAGE:
                new PingService().receiveInstructionAndStorage(jsonString, context);
                break;
            case ConstantUtils.DOWNLOAD:
                new PingService().receiveInstructionAndStorage(jsonString, context);
                break;
            default:
                break;
        }

    }

    @Override
    public void getLastestTask(String content) {

    }


    public void startTask(Context context) {
        new TaskTriggerService().startTask(context);
    }


}
