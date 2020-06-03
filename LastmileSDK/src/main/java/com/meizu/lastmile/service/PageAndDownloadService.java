package com.meizu.lastmile.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.meizu.lastmile.constants.ConstantUtils;
import com.meizu.lastmile.Utils.DatabaseHelper;
import com.meizu.lastmile.requestObj.PageRequestObject;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @Author： huangyongwen
 * @CreateDate： 2020/5/18 21:12
 * @Descriotion：接收任务，并存储在本地
 */

public class PageAndDownloadService extends Thread {

    private String TAG = Thread.currentThread().getName() + "--->>>LastMileSDK--->>> PageAndDownloadService";

    private Context context;
    private String jsonString;

    public PageAndDownloadService(String jsonString, Context context) {
        this.context = context;
        this.jsonString = jsonString;
    }

    @Override
    public void run() {
        receiveInstructionAndStorage();
    }


    /**
     * @Author huangyongwen
     * @CreateDate 2020/5/27 16:41
     * @Description page任务接收
     */
    public void receiveInstructionAndStorage() {

        if (StringUtils.isBlank(jsonString)) {
            return;
        }

        PageRequestObject pageRequestObject = JSON.parseObject(jsonString, PageRequestObject.class);
        //任务id
        String taskId = pageRequestObject.getTaskId();
        String taskType = pageRequestObject.getTaskType();
        if (StringUtils.isBlank(taskId) || StringUtils.isBlank(pageRequestObject.getUrl()) || StringUtils.isBlank(pageRequestObject.getUrl())) {
            return;
        }

        String groupsJsonString = JSON.toJSONString(pageRequestObject.getGroups());

        String tableStructure = "create table " + ConstantUtils.T_PAGE_DOWNLOAD + "(" +
                "taskId varchar(50) PRIMARY KEY NOT NULL," +
                "taskType varchar(50)," +
                "url varchar(500)," +
                "connectTimeout varchar(10)," +
                "maxTimeout varchar(10)," +
                "useRedirect varchar(10)," +
                "httpHeaders varchar(500)," +
                "hijacking status int NOT NULL default 0," +
                "command text," +
                "groups varchar(500)," +
                "expectContaining varchar(100)," +

                "lastExecuteTime varchar(30)," +

                "expireFrom varchar(30)," +
                "expireTo varchar(30)," +

                "monitorFrequency varchar(30)," +

                "executeTimeStart varchar(30)," +
                "executeTimeEnd varchar(30)," +
                "isExecute varchar(5)," +
                "status int NOT NULL default 1" +
                ")";

        StringBuffer command = new StringBuffer();

        command.append("curl");

        if (pageRequestObject.getUseRedirect()) {
            command.append(" -L ");
        }

        List<String> headerList = pageRequestObject.getHttpHeaders();
        String headerListJsonString = JSON.toJSONString(headerList);
        for (String header : headerList) {
            command.append(" -H '" + header + "'");
        }
        command.append(" --connect-timeout " + pageRequestObject.getConnectTimeout());
        command.append(" --max-time " + pageRequestObject.getMaxTimeout());

        //
        command.append(" -o /dev/null -s -w  ");
        //参数
//        command.append("response_code:'\\t\\t'%{response_code}'\\n'content_type:'\\t\\t'%{content_type}'\\n'" +
//                "time_namelookup:'\\t'%{time_namelookup}'\\n'time_redirect:'\\t\\t'%{time_redirect}'\\n'" +
//                "num_redirects:'\\t\\t'%{num_redirects}'\\n'time_connect:'\\t\\t'%{time_connect}'\\n'" +
//                "time_appconnect:'\\t'%{time_appconnect}'\\n'time_pretransfer:'\\t'%{time_pretransfer}'\\n'" +
//                "time_starttransfer:'\\t'%{time_starttransfer}'\\n'time_total:'\\t\\t'%{time_total}'\\n'" +
//                "size_header:'\\t\\t'%{size_header}'\\n'size_download:'\\t\\t'%{size_download}'\\n'" +
//                "speed_download:'\\t\\t'%{speed_download}'\\n' ");
        command.append("%{response_code}:%{content_type}:%{time_namelookup}:%{time_redirect}:%{num_redirects}:%{time_connect}:" +
                "%{time_appconnect}:%{time_pretransfer}:%{time_starttransfer}:%{time_total}:%{size_header}:%{size_download}:%{speed_download}'\\n' ");

        command.append(pageRequestObject.getUrl());

        System.out.println(command);

        //入库
        //1. 获取本地数据库
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        //取得一个的数据库对象
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            //初始化任务
            ContentValues values = new ContentValues();
            //像ContentValues中存放数据
            values.put("taskId", taskId);
            values.put("taskType", pageRequestObject.getTaskType());
//            values.put("connectTimeout", pageRequestObject.getConnectTimeout());
//            values.put("maxTimeout", pageRequestObject.getMaxTimeout());
//            values.put("useRedirect", pageRequestObject.getUseRedirect());
//            values.put("httpHeaders", headerListJsonString);
//            values.put("hijacking", pageRequestObject.getHijacking());
            values.put("command", command.toString());
            values.put("groups", groupsJsonString);

            values.put("expectContaining", pageRequestObject.getExpectContaining());

            values.put("lastExecuteTime", pageRequestObject.getLastExecuteTime());
            values.put("expireFrom", pageRequestObject.getExpireFrom());
            values.put("expireTo", pageRequestObject.getExpireTo());

            values.put("monitorFrequency", pageRequestObject.getMonitorFrequency());
            values.put("executeTimeStart", pageRequestObject.getExecuteTimeStart());
            values.put("executeTimeEnd", pageRequestObject.getExecuteTimeEnd());
            values.put("isExecute", pageRequestObject.getIsExecute());


            //2. 获取表是否存在，不存在则创建
            if (!dbHelper.isTableExist(db, ConstantUtils.T_PAGE_DOWNLOAD)) {
                Log.i(TAG, "创建表》》》》》》");
                dbHelper.createTable(db, tableStructure);
                //不存在任务，插入任务
                Log.i(TAG, "任务不存在，插入任务》》》》》");
                dbHelper.insert(db, ConstantUtils.T_PAGE_DOWNLOAD, values);
            } else {
                //3.查任务是否存在
                String[] placeholderValues = new String[]{taskId, taskType};
                String selection = "taskId=? AND taskType=?";
                Boolean IsHasTaskId = dbHelper.queryTaskIdSQL(db, ConstantUtils.T_PAGE_DOWNLOAD, new String[]{"taskId"}, selection, placeholderValues);
                if (IsHasTaskId) {
                    //已经存在任务，更新任务
                    Log.i(TAG, "任务已存在，更新任务》》》》》");
                    String[] condition = new String[]{taskId, taskType};
                    String whereClause = "taskId =? AND taskType =?";
                    dbHelper.update(db, ConstantUtils.T_PAGE_DOWNLOAD, values, whereClause, condition);
                } else {
                    Log.i(TAG, "任务不存在，插入任务》》》》》");
                    dbHelper.insert(db, ConstantUtils.T_PAGE_DOWNLOAD, values);
                }
            }

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        } finally {
            //关闭数据库
            if (db != null) {
                db.close();
            }
        }
    }
}
