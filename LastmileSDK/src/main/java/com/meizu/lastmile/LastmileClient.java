package com.meizu.lastmile;

import android.content.Context;

import com.meizu.lastmile.service.TaskTriggerService;

/**
 * Created by huangyongwen on 2020/5/13.
 */

public class LastmileClient implements Lastmile {

    @Override
    public void reviceInstructions(){
//        new PingService().
    }

    @Override
    public void getLastestTask() {

    }


    public void startTask(Context context){
        new TaskTriggerService().startTask(context);
    }


}
