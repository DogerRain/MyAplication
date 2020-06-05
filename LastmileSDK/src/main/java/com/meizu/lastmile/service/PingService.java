package com.meizu.lastmile.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.meizu.lastmile.Utils.DatabaseHelper;
import com.meizu.lastmile.constants.ConstantUtils;
import com.meizu.lastmile.requestObj.PingRequestObject;

import org.apache.commons.lang3.StringUtils;

/**
 * @Author： huangyongwen
 * @CreateDate： 2020/5/18 21:12
 * @Descriotion：接收任务，并存储在本地
 */

public class PingService {

    private String TAG = Thread.currentThread().getName() + "--->>>LastMileSDK--->>> PingService";

    private Context context;
    private String pingJsonString;

    public PingService(String pingJsonString, Context context) {
        this.context = context;
        this.pingJsonString = pingJsonString;
    }


    public void receiveInstructionAndStorage() {

     /*   Boolean isLackPerssion = new PermissionsChecker(context).checkLackWritePermission();
        if (isLackPerssion) {
            Log.i(TAG,"没有读写文件的权限");
            return;
        }*/

        if (StringUtils.isBlank(pingJsonString)) {
            return;
        }

        PingRequestObject pingRequestObject = JSON.parseObject(pingJsonString, PingRequestObject.class);


        if (StringUtils.isBlank(pingRequestObject.getHost())
                && StringUtils.isBlank(pingRequestObject.getHostName())
                ) {
            Log.i(TAG, "ping目标ip为空");
            return;
        }

        //任务id
        String taskId = pingRequestObject.getTaskId();
        String taskType = pingRequestObject.getTaskType();
        if (StringUtils.isBlank(taskId)) {
            return;
        }

        String groupsJsonString = JSON.toJSONString(pingRequestObject.getGroups());

        String tableStructure = "create table " + ConstantUtils.T_PING + "(" +
                "taskId varchar(50) PRIMARY KEY NOT NULL," +
                "taskType varchar(50)," +
                "taskName varchar(150)," +
                "timeout varchar(10)," +
                "count varchar(10)," +
                "size varchar(10)," +
                "interval varchar(10)," +
                "host varchar(30)," +
                "command varchar(500)," +
                "groups varchar(500)," +
                "tcpPing int ," +
                "supportIPv6 varchar(50)," +
                "dnsMatch varchar(30)," +
                "lastExecuteTime varchar(30)," +

                "expireFrom varchar(30)," +
                "expireTo varchar(30)," +
                "monitorFrequency varchar(30)," +

                "executeTimeStart varchar(30)," +
                "executeTimeEnd varchar(30)," +
                "isExecute varchar(5)," +
                "status int NOT NULL default 1" +
                ")";

//                "status int default 1)";

//        tableStructure= "create table user(id int,name varchar(20))";

        String target = pingRequestObject.getHost() != null ? pingRequestObject.getHost() : pingRequestObject.getHostName();
        StringBuffer command = new StringBuffer();
        command.append("ping");
        //ping次数
        command.append(" -c " + pingRequestObject.getCount());
        //windows是 -l ；Linux是 -s
        command.append(" -s " + pingRequestObject.getSize());
        //单位是秒 超时时间
        command.append(" -w " + pingRequestObject.getTimeout());
        //发送数据包的间隔， 单位是秒
        command.append(" -i " + pingRequestObject.getInterval());
        //目标
        command.append(" " + target);

        //1. 获取本地数据库
        DatabaseHelper dbHelper1 = new DatabaseHelper(context);
        //取得一个的数据库对象
        SQLiteDatabase db1 = dbHelper1.getWritableDatabase();
        try {
            //插入任务
            ContentValues values = new ContentValues();
            //像ContentValues中存放数据
            values.put("taskId", pingRequestObject.getTaskId());
            values.put("taskType", pingRequestObject.getTaskType());
            values.put("taskName", pingRequestObject.getTaskName());
            values.put("timeout", pingRequestObject.getTimeout());
            values.put("count", pingRequestObject.getCount());
            values.put("size", pingRequestObject.getSize());
            values.put("interval", pingRequestObject.getInterval());
            values.put("host", target);
            values.put("command", command.toString());
            values.put("groups", groupsJsonString);
            values.put("tcpPing", pingRequestObject.getTcpPing());
            values.put("supportIPv6", pingRequestObject.getSupportIPv6());
            values.put("dnsMatch", pingRequestObject.getDnsMatch());
//            values.put("lastExecuteTime", pingRequestObject.getLastExecuteTime());
            values.put("expireFrom", pingRequestObject.getExpireFrom());
            values.put("expireTo", pingRequestObject.getExpireTo());

            values.put("monitorFrequency", pingRequestObject.getMonitorFrequency());
            values.put("executeTimeStart", pingRequestObject.getExecuteTimeStart());
            values.put("executeTimeEnd", pingRequestObject.getExecuteTimeEnd());
            values.put("isExecute", pingRequestObject.getIsExecute());
            values.put("status", pingRequestObject.getStatus());

            //2. 获取表是否存在，不存在则创建
            if (!dbHelper1.isTableExist(db1, ConstantUtils.T_PING)) {
                Log.i(TAG, "创建表》》》》》》");
                dbHelper1.createTable(db1, tableStructure);
            }

            //3.查任务是否存在
//            String queryTaskIdSQL = "select * from " + ConstantUtils.T_PING + " where taskId=?";
            //3.查任务是否存在
            String[] placeholderValues = new String[]{taskId, taskType};
            String selection = "taskId=? AND taskType=?";
            Boolean IsHasTaskId = dbHelper1.queryTaskIdSQL(db1, ConstantUtils.T_PING, new String[]{"taskId"}, selection, placeholderValues);
            if (IsHasTaskId) {
                //已经存在任务，更新任务
                Log.i(TAG, taskType + "任务已存在，更新任务》》》》》");
                String[] condition = new String[]{taskId, taskType};
                String whereClause = "taskId =? AND taskType =?";
                dbHelper1.update(db1, ConstantUtils.T_PING, values, whereClause, condition);
            } else {
                //不存在任务，插入任务
                Log.i(TAG, "任务不存在，插入任务》》》》》");
                dbHelper1.insert(db1, ConstantUtils.T_PING, values);
            }

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        } finally {
            //关闭数据库
            if (db1 != null) {
                db1.close();
            }
        }
    }
}
