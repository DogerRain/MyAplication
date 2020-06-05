package com.meizu.lastmile.service;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.meizu.lastmile.Utils.CommonUtils;
import com.meizu.lastmile.constants.ConstantUtils;
import com.meizu.lastmile.requestObj.Instruction;
import com.meizu.lastmile.requestObj.Options;
import com.meizu.statsapp.v3.PkgType;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

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

    private TaskTriggerService() {

    }

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
        if (StringUtils.isBlank(taskType)) {
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
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)//设置连接超时时间
                .readTimeout(10, TimeUnit.SECONDS)//设置读取超时时间
                .build();
        Request request = new Request.Builder().url(ConstantUtils.REMOTE_URL)
                .get().build();
        Call call = client.newCall(request);
        String lastestJsonString = "";
        try {
            Response response = call.execute();
            lastestJsonString = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            if (e instanceof SocketTimeoutException) {//判断超时异常
                Log.e(TAG, "连接超时", e);
            }
            if (e instanceof ConnectException) {//判断连接异常，报Failed to connect to XXX.XXX.XXX.XXX
                Log.e(TAG, "连接异常", e);
            }
        } catch (Exception e) {
            Log.e(TAG, "未知错误", e);
        }
        String JsonArrayString = "[\n" +
                "{\n" +
                "    \"taskId\": 1234566,\n" +
                "\"taskName\":\"ping测试\",\n" +
                "    \"taskType\": \"ping\",\n" +
                "    \"groups\": [\n" +
                "        {\n" +
                "            \"idc\": \"ns\",\n" +
                "            \"isp\": [\n" +
                "                \"telecom\",\n" +
                "                \"unicom\"\n" +
                "            ],\n" +
                "            \"cities\": [\n" +
                "                \"zhuhai\",\n" +
                "                \"guangzho\"\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"idc\": \"bj\",\n" +
                "            \"isp\": [\n" +
                "                \"mobile\"\n" +
                "            ],\n" +
                "            \"cities\": [\n" +
                "                \"beijing\",\n" +
                "                \"tianjin\"\n" +
                "            ]\n" +
                "        }\n" +
                "    ],\n" +
                "    \"host\": \"14.152.74.1\",\n" +
                "    \"timeout\": 10,\n" +
                "    \"size\": 32,\n" +
                "    \"count\": 4,\n" +
                "    \"tcpPing\": false,\n" +
                "    \"interval\": 0.2,\n" +
                "    \"supportIPv6\": 2,\n" +
                "    \"dnsMatch\": 0,\n" +
                "    \"monitorFrequency\": \"\",\n" +
                "    \"expireFrom\": \"2019-12-20 00:00:00\",\n" +
                "    \"expireTo\": \"2020-12-20 23:59:59\",\n" +
                "    \"isExecute\": false,\n" +
                "    \"executeTimeStart\": \"20\",\n" +
                "    \"executeTimeEnd\": \"24\"\n" +
                "},\n" +
                "{\n" +
                "    \"taskId\": 1234568,\n" +
                "\"taskName\":\"文件下载\",\n" +
                "    \"taskType\": \"download\",\n" +
                "    \"groups\": [\n" +
                "        {\n" +
                "            \"idc\": \"ns\",\n" +
                "            \"isp\": [\n" +
                "                \"telecom\",\n" +
                "                \"unicom\"\n" +
                "            ],\n" +
                "            \" cities\": [\n" +
                "                \"zhuhai\",\n" +
                "                \"guangzho\"\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"idc\": \"bj\",\n" +
                "            \"isp\": [\n" +
                "                \"mobile\"\n" +
                "            ],\n" +
                "            \"cities\": [\n" +
                "                \"beijing\",\n" +
                "                \"tianjin\"\n" +
                "            ]\n" +
                "        }\n" +
                "    ],\n" +
                "    \"url\": \"http://mirrors.163.com/mysql/Downloads/MySQL-6.0/mysql-6.0.11-alpha.zip\",\n" +
                "    \"connectTimeout\": 5,\n" +
                "    \"maxTimeout\": 20,\n" +
                "    \"useRedirect\": true,\n" +
                "    \"httpHeaders\": [\n" +
                "        \"User-Agent:mz-lastmile\"\n" +
                "    ],\n" +
                "    \"hijacking\": false,\n" +
                "    \"expectHeaders\": [\n" +
                "        \"Custome-Header:hello world\"\n" +
                "    ],\n" +
                "    \"md5\": \"a957843erse828faui1o109pqik38821\",\n" +
                "    \"monitorFrequency\": \"\",\n" +
                "    \"expireFrom\": \"2019-12-20 00:00:00\",\n" +
                "    \"expireTo\": \"2020-12-20 23:59:59\",\n" +
                "    \"isExecute\": true,\n" +
                "    \"executeTimeStart\": \"0\",\n" +
                "    \"executeTimeEnd\": \"24\"\n" +
                "},\n" +
                "{\n" +
                "    \"taskId\": 565879625,\n" +
                "\"taskName\":\"图片下载\",\n" +
                "    \"taskType\": \"page\",\n" +
                "    \"groups\": [\n" +
                "        {\n" +
                "            \"idc\": \"ns\",\n" +
                "            \"isp\": [\n" +
                "                \"telecom\",\n" +
                "                \"unicom\"\n" +
                "            ],\n" +
                "            \"cities\": [\n" +
                "                \"zhuhai\",\n" +
                "                \"guangzho\"\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"idc\": \"bj\",\n" +
                "            \"isp\": [\n" +
                "                \"mobile\"\n" +
                "            ],\n" +
                "            \"cities\": [\n" +
                "                \"beijing\",\n" +
                "                \"tianjin\"\n" +
                "            ]\n" +
                "        }\n" +
                "    ],\n" +
                "    \"url\": \"https://fms.res.meizu.com/dms/2020/05/08/041087f7-680e-40fe-a2cc-bcdb81931aa3.png\",\n" +
                "    \"connectTimeout\": 5,\n" +
                "    \"maxTimeout\": 10,\n" +
                "    \"useRedirect\": true,\n" +
                "    \"httpHeaders\": [\n" +
                "        \"User-Agent:mz-lastmile\"\n" +
                "    ],\n" +
                "    \"hijacking\": false,\n" +
                "    \"monitorFrequency\": \"\",\n" +
                "    \"expireFrom\": \"2019-12-20 00:00:00\",\n" +
                "    \"expireTo\": \"2020-12-20 23:59:59\",\n" +
                "    \"isExecute\": true,\n" +
                "    \"executeTimeStart\": \"0\",\n" +
                "    \"executeTimeEnd\": \"24\"\n" +
                "}\n" +
                "\n" +
                "]";
//        return JsonArrayString;
        return lastestJsonString;
    }


    public void startLocalTask(final String eventName, final String pageName, final PkgType pkgType, final String key, final Options options) {
        if (StringUtils.isBlank(eventName)) {
            throw new IllegalArgumentException("eventName is null");
        }
        if (StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("nomal key is null");
        }
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int flag = CommonUtils.getNetWorkStart(context);
                if (flag == CommonUtils.NETWORK_NONE) {
                    return;
                }
                /**
                 * 取remote任务，存放在本地
                 */
                try {
                    String lastestJsonString = getRemoteLastestTask();
                    if (StringUtils.isBlank(lastestJsonString)) {
                        Log.e(TAG, "请求回参为空");
                        return;
                    }
                    JSONObject root = new JSONObject().parseObject(lastestJsonString);// 将json格式的字符串转换成json

                    if (root.getString("code").equals("200")) {
                        JSONArray objects = JSON.parseArray(root.getString("value"));
                        for (int i = 0; i < objects.size(); i++) {
                            //通过数组下标取到object，使用强转转为JSONObject，之后进行操作
                            String jsonString = JSON.toJSONString(objects.get(i));
                            receiveTaskSynchronize(jsonString);
                        }
                    } else {
                        Log.e(TAG, "返回出错：" + root.getString("message"));
                        return;
                    }
                } catch (Exception e) {
                    Log.e(TAG, "JSON参数解析错误", e);
                    return;
                }
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


    public static void main(String[] args) {
        TaskTriggerService taskTriggerService = new TaskTriggerService();
        String lastestJsonString = taskTriggerService.getRemoteLastestTask();
        JSONObject root = new JSONObject().parseObject(lastestJsonString);// 将json格式的字符串转换成json
        if (root.getString("code").equals("200")) {
            JSONArray objects = JSON.parseArray(root.getString("value"));
            for (int i = 0; i < objects.size(); i++) {
                //通过数组下标取到object，使用强转转为JSONObject，之后进行操作
                String jsonString = JSON.toJSONString(objects.get(i));
                System.out.println(jsonString);
            }
        } else {
            System.out.println("返回出错：" + root.getString("message"));
            return;
        }
    }

}
