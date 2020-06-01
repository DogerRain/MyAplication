package com.meizu.lastmile.service;

        import android.content.Context;

        import com.alibaba.fastjson.JSON;
        import com.meizu.lastmile.Utils.CommonUtils;
        import com.meizu.lastmile.constants.ConstantUtils;
        import com.meizu.lastmile.requestObj.Instruction;
        import com.meizu.lastmile.requestObj.Options;
        import com.meizu.statsapp.v3.PkgType;

/**
 * @Author: huangyongwen
 * @CreateDate: 2020/5/20 16:56
 * @Description: 任务触发service
 */

public class TaskTriggerService {
    private Context context;
    private String jsonString;
//    private Application application;
//    private Options options;
//    private PkgType pkgType;
//    private String key;
//    private String evetName;

    public TaskTriggerService(Context context, String jsonString) {
        this.context = context;
        this.jsonString = jsonString;
    }

    public TaskTriggerService(Context context) {
        this.context = context;
    }

//    private void TaskTriggerService(Options options, PkgType pkgType, String key, String evetName) {
//        this.options = options;
//        this.pkgType = pkgType;
//        this.key = key;
//        this.evetName = evetName;
//    }


    public void receiveTask() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //起一个线程
                Instruction instruction = JSON.parseObject(jsonString, Instruction.class);
                String taskType = instruction.getTaskType();
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
        });
        thread.start();
    }


    public void startTask(final String eventName, final String pageName, final PkgType pkgType, final String key, final Options options) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int flag = CommonUtils.getNetWorkStart(context);
                switch (flag) {
                    case CommonUtils.NETWORW_WIFI:
                        startPingTask(eventName, pageName, pkgType, key, options);
//                        startSingleWebTask(eventName, pageName, pkgType, key, options);
//                        startFileDownloadTask(eventName, pageName, pkgType, key, options);
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
