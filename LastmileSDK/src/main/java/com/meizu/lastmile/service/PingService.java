package com.meizu.lastmile.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.meizu.lastmile.Utils.DatabaseHelper;
import com.meizu.lastmile.requestObj.NodeGroup;
import com.meizu.lastmile.requestObj.Ping.PingRequestObject;
import com.meizu.lastmile.responseObj.PingResponseObject;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author： Administrator
 * @CreateDate： 2020/5/18 21:12
 * @Descriotion：接收任务，并存储在本地
 */

public class PingService {
    public PingResponseObject receiveInstructionAndStorage(PingRequestObject pingRequestObject, Context context) {
        if (pingRequestObject == null) {
            return null;
        }
        if (StringUtils.isBlank(pingRequestObject.getIp())
                && StringUtils.isBlank(pingRequestObject.getHostName())
                ) {
            return null;
        }

        //任务id
        String pingTaskId = pingRequestObject.getPingTaskId();
        if (StringUtils.isBlank(pingTaskId)) {
            return null;
        }


        String databaseName = "lastmile";
        String tableName = "t_ping_command";
        String tableStructure = "create table " + tableName + "(" +
//                "pingTaskId INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id INTEGER ," +
                "timeout varchar(10)," +
                "count varchar(10)," +
                "packageSize varchar(10)," +
                "ip varchar(30)," +
                "hostName varchar(100)," +
                "command text," +
                "lastExecuteTime varchar(30)," +

                "monitorName varchar(50)," +
                "monitorType INTEGER," +
                "monitorOption varchar(50)," +
                "validTimeStart varchar(30)," +
                "validTimeEnd varchar(30)," +
                "nodeGroup text," +
                "end_time varchar(30)," +
                "end_time varchar(30)," +
                "status INTEGER default 1)";


        String target = pingRequestObject.getIp() != null ? pingRequestObject.getIp() : pingRequestObject.getHostName();

        StringBuffer command = new StringBuffer();
        command.append("ping " + target);
        //ping次数 默认是4次
        if (StringUtils.isNotBlank(pingRequestObject.getCount())) {
            command.append(" -c " + pingRequestObject.getCount());
        }
        //windows是 -l ；Linux是 -s
        if (StringUtils.isNotBlank(pingRequestObject.getPackageSize())) {
            command.append(" -s " + pingRequestObject.getPackageSize());
        }
        //单位是ms
        if (StringUtils.isNotBlank(pingRequestObject.getTimeout())) {
            command.append(" -w " + pingRequestObject.getTimeout());
        }

        //1. 获取本地数据库
        DatabaseHelper dbHelper1 = new DatabaseHelper(context);
        //取得一个的数据库对象
        SQLiteDatabase db1 = dbHelper1.getWritableDatabase();

        //插入任务
        ContentValues values = new ContentValues();
        //像ContentValues中存放数据
        values.put("id", pingRequestObject.getPingTaskId());
        values.put("timeout", pingRequestObject.getTimeout());
        values.put("count", pingRequestObject.getCount());
        values.put("packageSize", pingRequestObject.getPackageSize());
        values.put("ip", pingRequestObject.getIp());
        values.put("hostName", pingRequestObject.getHostName());
        values.put("lastExecuteTime", pingRequestObject.getLastExecuteTime());


        values.put("monitorName", pingRequestObject.getMonitorName());
        values.put("monitorType", pingRequestObject.getMonitorType());
        values.put("monitorOption", pingRequestObject.getMonitorOption());
        values.put("validTimeStart", pingRequestObject.getValidTimeStart());
        values.put("validTimeEnd", pingRequestObject.getValidTimeEnd());
        String nodeGroup = JSON.toJSONString(pingRequestObject.getNodeGroup());
        values.put("nodeGroup", nodeGroup);


        values.put("monitorFrequency", pingRequestObject.getMonitorFrequency());
        values.put("executeTimeStart", pingRequestObject.getExecuteTimeStart());
        values.put("executeTimeEnd", pingRequestObject.getExecuteTimeEnd());
        values.put("IsExecute", pingRequestObject.getIsExecute());


        //2. 获取表是否存在，不存在则创建
        if (!dbHelper1.IsTableExist(db1, tableName)) {
            dbHelper1.createTable(db1, tableStructure);
        }

        //3.查任务是否存在
        String queryTaskIdSQL = "select pingTaskId from ? where pingTaskId=?";
        Boolean IsHasTaskId = dbHelper1.queryTaskIdSQL(db1, queryTaskIdSQL, new String[]{tableName, pingTaskId});
        if (IsHasTaskId) {
            //已经存在任务，删除任务，插入新任务
            //插入任务
            String[] deleteValues = new String[]{pingTaskId};
            dbHelper1.delete(db1, tableName, deleteValues);
        }
        //不存在任务，插入任务
        dbHelper1.insert(db1, tableName, values);

        //关闭数据库
        if (db1 != null) {
            db1.close();
        }


        return null;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        PingRequestObject pingRequestObject = new PingRequestObject();
        List<String> city = new ArrayList<>();
        city.add("HN");
        city.add("GZ");
        NodeGroup nodeGroup = NodeGroup.builder().nodeGroupCount(1).nodeGroupName("2").city(city).build();
        pingRequestObject.setNodeGroup(nodeGroup);

        JSONObject jsonObj = (JSONObject) JSON.toJSON(pingRequestObject);
        String rest = JSON.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
        System.out.println(rest);
    }
}
