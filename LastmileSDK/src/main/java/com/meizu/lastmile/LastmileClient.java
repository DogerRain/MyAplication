package com.meizu.lastmile;

import android.content.Context;

import com.meizu.lastmile.requestObj.Options;
import com.meizu.lastmile.service.ReceiveTaskService;
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
        new ReceiveTaskService(context, jsonString).receiveInstructions();
    }

    @Override
    public void getLastestTask() {

    }


    @Override
    public void startLocalTask(Options options) {
        TaskTriggerService taskTriggerService = new TaskTriggerService(context);
        taskTriggerService.startTask(options);
    }


}
