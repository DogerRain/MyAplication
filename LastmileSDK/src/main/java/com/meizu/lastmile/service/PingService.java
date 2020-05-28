package com.meizu.lastmile.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.meizu.lastmile.Utils.ConstantUtils;
import com.meizu.lastmile.Utils.DatabaseHelper;
import com.meizu.lastmile.requestObj.Options;
import com.meizu.lastmile.requestObj.PingRequestObject;
import com.meizu.lastmile.responseObj.PingResponseObject;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * @Author： huangyongwen
 * @CreateDate： 2020/5/18 21:12
 * @Descriotion：接收任务，并存储在本地
 */

public class PingService extends Thread {

    private String TAG = "LastMileSDK》》》 PingService";

    private Context context;
    private String pingJsonString;

    public PingService(String pingJsonString, Context context ) {
        this.context = context;
        this.pingJsonString = pingJsonString;
    }

    @Override
    public void run() {
        receiveInstructionAndStorage();
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
            values.put("expireFrom", pingRequestObject.getExpireFrom());
            values.put("expireTo", pingRequestObject.getExpireTo());

            values.put("monitorFrequency", pingRequestObject.getMonitorFrequency());
            values.put("executeTimeStart", pingRequestObject.getExecuteTimeStart());
            values.put("executeTimeEnd", pingRequestObject.getExecuteTimeEnd());
            values.put("isExecute", pingRequestObject.getIsExecute());


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
                Log.i(TAG, "任务已存在，更新任务》》》》》");
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

    /**
     * 组装ping结果
     *
     * @param stringBuffer
     * @param text
     */
    private static void append(StringBuffer stringBuffer, String text) {
        if (stringBuffer != null) {
            stringBuffer.append(text + "\n");
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


        /*
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
        String command = "ping -c 5 1212.com";
//        String command = "ping -c 5 hyete123.com";

        new PingService().AnalysePingCommand(command);*/
        BufferedReader successReader = new BufferedReader(new FileReader("C:\\Users\\huangyongwen\\Desktop\\test.txt"));

        PingResponseObject pingResponseObject = new PingResponseObject();
        pingResponseObject.setResultBuffer(new StringBuffer());
        String line = "";

        while ((line = successReader.readLine()) != null) {
            append(pingResponseObject.getResultBuffer(), line);
            //解析每一行
            if (line.contains("packets transmitted,")) {
                String transmittedPackages = line.substring(0, line.indexOf("packets transmitted,"));
                String receivedPackages = line.substring(line.indexOf("packets transmitted,") + "packets transmitted,".length(), line.indexOf("received"));
                String packetLossRate = line.substring(line.indexOf("received,") + "received,".length(), line.indexOf("packet loss"));
                String sendUsedTime = line.substring(line.indexOf("time") + "time".length(), line.indexOf("ms"));
            }
            if (line.contains("rtt min/avg/max/mdev")) {
                String detailString = line.substring(line.indexOf("rtt min/avg/max/mdev =") + "rtt min/avg/max/mdev =".length(), line.indexOf("ms"));
                detailString = detailString.trim();
                String[] split = detailString.split("/");
                String minDelayedTime = split[0];
                String avgDelayedTime = split[1];
                String maxDelayedTime = split[2];
                String mdevDelayedTime = split[3];
            }
        }

//        System.out.println(line.indexOf("packets transmitted,"));
//        int i = line.indexOf(",");
//        System.out.println(line.indexOf(","));
//        System.out.println(line.indexOf(",",i+1));
//        System.out.println(line.indexOf(" 0%"));

//        System.out.println(line.substring(line.indexOf("packets transmitted,".length()),line.indexOf("received")));

    }
}
