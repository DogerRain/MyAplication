package com.meizu.lastmile.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.meizu.lastmile.Utils.CommonUtils;
import com.meizu.lastmile.Utils.ConstantUtils;
import com.meizu.lastmile.Utils.DatabaseHelper;
import com.meizu.lastmile.responseObj.PingResponseObject;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author: huangyongwen
 * @CreateDate: 2020/5/25 14:52
 * @Description: 执行本地任务
 */

public class PageRunLocalTaskService extends Thread {
    private String TAG = "LastMileSDK》》》 PingRunLocalTaskService";

    private Context context;

    public PageRunLocalTaskService(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        getLocalPingTask();
    }


    /**
     * 触发调用本地Ping任务
     */
    public void getLocalPingTask() {

        //1. 获取本地数据库
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        //2. 取得一个的数据库对象
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        //3 判断表是否存在
        if (!dbHelper.isTableExist(db, ConstantUtils.T_PING)) {
            //不存在证明用户可能清除了数据
            return;
        }
        //4.查询本地任务，把命令取出
        String[] selectColumnName = new String[]{"taskId", "command", "lastExecuteTime", "expireFrom", "expireTo", "groups", "monitorFrequency", "executeTimeStart", "executeTimeEnd", "isExecute"};
        List<Map<String, String>> list = dbHelper.queryTask(db, ConstantUtils.T_PING, selectColumnName, null, null);
        for (Map<String, String> hashmap : list) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date start = new Date();
                Long time = start.getTime();
                //1 判断是否在有效期内
                String expireFrom = hashmap.get("expireFrom");
                String expireTo = hashmap.get("expireTo");
                if (StringUtils.isNotBlank(expireFrom)) {
                    long subHourExpireFrom = CommonUtils.getHourSub(formatter.parse(expireFrom), start);
                    if (subHourExpireFrom < 0) {
                        continue;
                    }
                }
                if (StringUtils.isNotBlank(expireTo)) {
                    long subHourExpireTo = CommonUtils.getHourSub(start, formatter.parse(expireTo));
                    if (subHourExpireTo < 0) {
                        continue;
                    }
                }

                //2 . 判断上一次执行时间是否在频率之内
                String lastExecuteTime = hashmap.get("lastExecuteTime");
                //判断是否首次
                if (StringUtils.isNotBlank(lastExecuteTime)) {
                    Date last = formatter.parse(lastExecuteTime);
                    long subHour = CommonUtils.getHourSub(start, last);
//                    monitorFrequency为空表示执行
                    if (StringUtils.isNotBlank(hashmap.get("monitorFrequency"))) {
                        long monitorFrequency = Long.parseLong(hashmap.get("monitorFrequency"));
                        if (subHour < monitorFrequency) {
                            //频率之内，不执行
                            continue;
                        }
                    }

                }


                //取监控计划
                String isExecute =hashmap.get("isExecute");

                //取command命令
                String commad = hashmap.get("command") + "";
                //解析命令
                PingResponseObject pingResponseObject = analysePingCommand(commad);

                System.out.println(pingResponseObject);
                //上传到大数据那边
//                pingResponseObjece


//                执行完成，更新上一次执行时间
                String startString = formatter.format(start);
                ContentValues values = new ContentValues();
                values.put("lastExecuteTime", startString);
                dbHelper.update(db, ConstantUtils.T_PING, values, new String[]{hashmap.get("taskId") + ""});
            } catch (ParseException e) {
                Log.e(TAG, "日期转换失败",e);
            } catch (Exception e) {
                Log.e(TAG, "未知错误",e);
            } finally {
                if (db != null) {
                    db.close();
                }
            }

        }
    }

    private PingResponseObject analysePingCommand(String command) {
        String line = null;
        Process process = null;
        BufferedReader successReader = null;
        StringBuffer resultStringBuffer = new StringBuffer("");
        PingResponseObject pingResponseObject = new PingResponseObject();
        pingResponseObject.setResultBuffer(resultStringBuffer);
        try {
            process = Runtime.getRuntime().exec(command);
            if (process == null) {
                Log.e(TAG, " ping fail:process is null.");
                append(pingResponseObject.getResultBuffer(), "ping fail:process is null.");
                pingResponseObject.setResult(false);
                return pingResponseObject;
            }

            successReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = successReader.readLine()) != null) {
                //规则解析
                getResultByMatchingRules(pingResponseObject, line);
            }
            // 0 表示执行成功，有返回
            int status = process.waitFor();
            if (status == 0) {
                Log.i(TAG, "exec cmd success:" + command);
                append(pingResponseObject.getResultBuffer(), "exec cmd success:" + command);
                pingResponseObject.setResult(true);
            } else {
                Log.e(TAG, "exec cmd fail.");
                append(pingResponseObject.getResultBuffer(), "exec cmd fail.");
                pingResponseObject.setResult(false);
            }
            Log.i(TAG, "exec finished.");
            append(pingResponseObject.getResultBuffer(), "exec finished.");

        } catch (IOException e) {
            Log.e(TAG, String.valueOf(e));
        } catch (InterruptedException e) {
            Log.e(TAG, String.valueOf(e));
        } finally {
            Log.i(TAG, "ping exit.");
            if (process != null) {
                process.destroy();
            }
            if (successReader != null) {
                try {
                    successReader.close();
                } catch (IOException e) {
                    Log.e(TAG, String.valueOf(e));
                }
            }
        }
        return pingResponseObject;
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


    private PingResponseObject getResultByMatchingRules(PingResponseObject pingResponseObject, String line) {
        if (line.contains("ping: unknown host")) {
            append(pingResponseObject.getResultBuffer(), line);
            pingResponseObject.setResult(false);
            return pingResponseObject;
        }
        //解析每一行
        if (line.contains("packets transmitted,")) {
            String transmittedPackages = line.substring(0, line.indexOf("packets transmitted,")).trim();
            String receivedPackages = line.substring(line.indexOf("packets transmitted,") + "packets transmitted,".length(), line.indexOf("received")).trim();
            String packetLossRate = line.substring(line.indexOf("received,") + "received,".length(), line.indexOf("packet loss")).trim();
            String sendUsedTime = line.substring(line.indexOf("time") + "time".length(), line.indexOf("ms")).trim();

            pingResponseObject.setTransmittedPackages(transmittedPackages);
            pingResponseObject.setReceivedPackages(receivedPackages);
            pingResponseObject.setPacketLossRate(packetLossRate);
            pingResponseObject.setSendUsedTime(sendUsedTime);
        }
        if (line.contains("rtt min/avg/max/mdev")) {
            String detailString = line.substring(line.indexOf("rtt min/avg/max/mdev =") + "rtt min/avg/max/mdev =".length(), line.indexOf("ms"));
            detailString = detailString.trim();
            String[] split = detailString.split("/");
            String minDelayedTime = split[0];
            String avgDelayedTime = split[1];
            String maxDelayedTime = split[2];
            String mdevDelayedTime = split[3];
            pingResponseObject.setMinDelayedTime(minDelayedTime);
            pingResponseObject.setAvgDelayedTime(avgDelayedTime);
            pingResponseObject.setMaxDelayedTime(maxDelayedTime);
            pingResponseObject.setMdevDelayedTime(mdevDelayedTime);
        }
        pingResponseObject.setResult(true);
        return pingResponseObject;
    }
}
