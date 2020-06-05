package com.meizu.lastmile;

import android.content.Context;

import com.meizu.lastmile.requestObj.Options;
import com.meizu.lastmile.service.TaskTriggerService;
import com.meizu.statsapp.v3.PkgType;


/**
 * Created by huangyongwen on 2020/5/13.
 */

public class LastmileClient {
    Context context;
    private volatile static LastmileClient lastmileClient;

    private LastmileClient(Context context) {
        this.context = context;
    }

    /**
     * 初始化 Lastmile
     *
     * @param context
     */
    public static void init(Context context) {
        if (lastmileClient == null) {
            synchronized (LastmileClient.class) {
                if (lastmileClient == null) {
                    lastmileClient = new LastmileClient(context);
                }
            }
        }
    }

    /**
     * 获得LastmileClient对象。
     *
     * @return LastmileClient
     */
    public static LastmileClient getInstance() {
        if (lastmileClient == null) {
            throw new IllegalStateException("LastmileClient is not initialised - invoke at least once with parameterised init");
        }
        return lastmileClient;
    }

    /**
     * push平台的content
     *
     * @param jsonString
     */
    public void reviceInstructions(String jsonString) {
        new TaskTriggerService(context).receiveTask(jsonString);
    }

    /**
     * 执行lastMile任务
     *
     * @param eventName 事件名称
     * @param pageName  事件发生的页面，可以为空
     * @param pkgType   app类型
     * @param key       nomal平台的 key
     * @param options   用户信息
     */
    public void runLocalTaskAndReport(String eventName, String pageName, PkgType pkgType, String key, Options options) {
        TaskTriggerService taskTriggerService = new TaskTriggerService(context);
        taskTriggerService.startLocalTask(eventName, pageName, pkgType, key, options);
    }

}
