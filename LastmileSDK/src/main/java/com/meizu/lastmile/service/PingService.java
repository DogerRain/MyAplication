package com.meizu.lastmile.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.meizu.lastmile.Utils.CommonUtils;
import com.meizu.lastmile.Utils.ConstantUtils;
import com.meizu.lastmile.Utils.DatabaseHelper;
import com.meizu.lastmile.requestObj.Group;
import com.meizu.lastmile.requestObj.PingRequestObject;
import com.meizu.lastmile.requestObj.TaskType;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author： huangyongwen
 * @CreateDate： 2020/5/18 21:12
 * @Descriotion：接收任务，并存储在本地
 */

public class PingService {

    private String TAG = "PingService";

    public void receiveInstructionAndStorage(String pingJsonString, Context context) {


        if (StringUtils.isBlank(pingJsonString)) {
            return;
        }

        PingRequestObject pingRequestObject = JSON.parseObject(pingJsonString, PingRequestObject.class);


        if (StringUtils.isBlank(pingRequestObject.getHost())
                && StringUtils.isBlank(pingRequestObject.getHostName())
                ) {
            return;
        }

        //任务id
        String pingTaskId = pingRequestObject.getTaskId();
        if (StringUtils.isBlank(pingTaskId)) {
            return;
        }


        String groupsJsonString = JSON.toJSONString(pingRequestObject.getGroups());


        String tableStructure = "create table " + ConstantUtils.T_PING + "(" +
//                "pingTaskId INTEGER PRIMARY KEY AUTOINCREMENT," +
                "taskId INTEGER ," +
                "taskType varchar(50)," +
                "timeout varchar(10)," +
                "count varchar(10)," +
                "size varchar(10)," +
                "interval varchar(10)," +
                "host varchar(30)," +
                "command varchar(500)," +
                "groups varchar(500)," +
                "tcpPing boolean default false," +
                "supportIPv6 varchar(50)," +
                "dnsMatch varchar(30)," +
                "lastExecuteTime varchar(30)," +
                "status INTEGER default 1)";
        String target = pingRequestObject.getHost() != null ? pingRequestObject.getHost() : pingRequestObject.getHostName();
        StringBuffer command = new StringBuffer();
        command.append("ping " + target);
        //ping次数
        command.append(" -c " + pingRequestObject.getCount());
        //windows是 -l ；Linux是 -s
        command.append(" -s " + pingRequestObject.getSize());
        //单位是秒 超时时间
        command.append(" -w " + pingRequestObject.getTimeout());
        //发送数据包的间隔， 单位是秒
        command.append(" -i " + pingRequestObject.getInterval());

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
            values.put("lastExecuteTime", pingRequestObject.getLastExecuteTime());

            values.put("monitorFrequency", pingRequestObject.getMonitorFrequency());
            values.put("executeTimeStart", pingRequestObject.getExecuteTimeStart());
            values.put("executeTimeEnd", pingRequestObject.getExecuteTimeEnd());
            values.put("IsExecute", pingRequestObject.getIsExecute());


            //2. 获取表是否存在，不存在则创建
            if (!dbHelper1.IsTableExist(db1, ConstantUtils.T_PING)) {
                dbHelper1.createTable(db1, tableStructure);
            }

            //3.查任务是否存在
            String queryTaskIdSQL = "select pingTaskId from ? where taskId=?";
            Boolean IsHasTaskId = dbHelper1.queryTaskIdSQL(db1, queryTaskIdSQL, new String[]{ConstantUtils.T_PING, pingTaskId});
            if (IsHasTaskId) {
                //已经存在任务，更新任务
                String[] condition = new String[]{pingTaskId};
                dbHelper1.update(db1, ConstantUtils.T_PING, values, condition);
            } else {
                //不存在任务，插入任务
                dbHelper1.insert(db1, ConstantUtils.T_PING, values);
            }

        } catch (Exception e) {
            Log.e(TAG, "创建本地任务失败");
        } finally {
            //关闭数据库
            if (db1 != null) {
                db1.close();
            }
        }


    }


    /**
     * 触发调用本地Ping任务
     */
    public void getLocalPingTask(Context context) {
        //1. 获取本地数据库
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        //2. 取得一个的数据库对象
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //3.查询本地任务，把命令取出
        String[] selectColumnName = new String[]{"taskId", "command", "lastExecuteTime", "groups", "monitorFrequency", "executeTimeStart", "executeTimeEnd", "IsExecute"};
        List<Map<String, String>> list = dbHelper.queryTask(db, ConstantUtils.T_PAGE, selectColumnName, null, null);
        for (Map hashmap : list) {
            //1 . 判断上一次执行时间是否在频率之内
            try {
                String lastExecuteTime = hashmap.get("lastExecuteTime") + "";
                Date last = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(lastExecuteTime);
                Date start = new Date();
                long subHour = CommonUtils.getHourSub(start, last);
                long monitorFrequency = Long.parseLong(hashmap.get("monitorFrequency") + "");
                if (subHour > monitorFrequency) {

                }
            } catch (ParseException e) {
                Log.e(TAG, "日期转换失败");
            } finally {
                if (db != null) {
                    db.close();
                }
            }

        }


    }


    public static void main(String[] args) throws IOException, InterruptedException {
       /* PingRequestObject pingRequestObject = new PingRequestObject();
        List<String> city = new ArrayList<>();
        city.add("HN");
        city.add("GZ");
        List<Group> group = new ArrayList<>();
        group.add(Group.builder().groupCount(1).idc("2").citis(city).build());
        pingRequestObject.setGroups(group);

        JSONObject jsonObj = (JSONObject) JSON.toJSON(pingRequestObject);
        String rest = JSON.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
        System.out.println(rest);

        try {
            InetAddress ip4 = Inet4Address.getLocalHost();
            System.out.println(ip4.getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }*/


        String jsonString = "\n" +
                "{\n" +
                "\t\"taskId\": 1234566, \n" +
                "  \"taskType\": \"ping\",\n" +
                "\t\"groups\": [\n" +
                "\t\t{\n" +
                "\t\t\t\"idc\": \"ns\",\n" +
                "\t\t\t\"isp\": [\"telecom\", \"unicom\"],\n" +
                "\t\t\t\"cities\": [\n" +
                "\t\t\t\t\"zhuhai\",\n" +
                "\t\t\t\t\"guangzho\"\n" +
                "\t\t\t]\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"idc\": \"bj\",\n" +
                "\t\t\t\"isp\": [\"mobile\"],\n" +
                "\t\t\t\"cities\": [\n" +
                "\t\t\t\t\"beijing\",\n" +
                "\t\t\t\t\"tianjin\"\n" +
                "\t\t\t]\n" +
                "\t\t}\n" +
                "  ],\n" +
                "        \n" +
                "  \"host\": \"14.152.74.1\",\n" +
                "  \"timeout\": 10,\n" +
                "    \"size\": 32,\n" +
                "    \"count\": 4,\n" +
                "    \"tcpPing\": false,\n" +
                "    \"interval\": 0.2,\n" +
                "    \"supportIPv6\": 2,\n" +
                "    \"dnsMatch\": 0\n" +
                "}";
//        System.out.println(jsonString);


        PingRequestObject pingRequestObject = JSON.parseObject(jsonString, PingRequestObject.class);

//        PingRequestObject pingRequestObject1  =PingRequestObject.builder().

        Group group = new Group();
//        group.

        System.out.println(pingRequestObject);

        System.out.println(JSON.toJSONString(pingRequestObject));


        System.out.println(TaskType.PING.getTaskType());

        Boolean flag = false;
        String a = flag + "";
        System.out.println(a);

        Map<String, String> hashMap = new HashMap<>();
        List<Map<String, String>> list = new ArrayList<>();
        hashMap.put("a", "a");
        hashMap.put("b", "2");
        list.add(hashMap);
        for (Map hashmap : list) {
            String aa = (String) hashmap.get("a");
            Long bb = Long.parseLong(hashmap.get("b") + "");
            System.out.println(aa);
            System.out.println(bb);
        }
    }
}
