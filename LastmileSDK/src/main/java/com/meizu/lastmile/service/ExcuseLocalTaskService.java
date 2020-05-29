package com.meizu.lastmile.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.meizu.lastmile.Utils.CommonUtils;
import com.meizu.lastmile.Utils.ConstantUtils;
import com.meizu.lastmile.Utils.DatabaseHelper;
import com.meizu.lastmile.Utils.ShellUtils;
import com.meizu.lastmile.requestObj.Group;
import com.meizu.lastmile.requestObj.Options;
import com.meizu.lastmile.responseObj.PageResponseObject;
import com.meizu.lastmile.responseObj.PingResponseObject;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * @Author huangyongwen
 * @CreateDate 2020/5/28 10:20
 * @params
 * @Description 运行本地任务的公共类
 */

public class ExcuseLocalTaskService extends Thread {

    private String TAG = "LastMileSDK》》》 ExcuseLocalTaskService";


    private Context context;
    private String taskType;
    private String tableName;
    private Options options;

    public ExcuseLocalTaskService(Context context, String taskType, String tableName, Options options) {
        this.context = context;
        this.taskType = taskType;
        this.tableName = tableName;
        this.options = options;

    }

    @Override
    public void run() {
        excuseLocalTask();
    }

    public void excuseLocalTask() {
        //1. 获取本地数据库
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        //2. 取得一个的数据库对象
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        //3 判断表是否存在
        if (!dbHelper.isTableExist(db, tableName)) {
            //不存在证明用户可能清除了数据
            return;
        }
        Log.i(TAG, "当前任务：" + taskType);
        //4.查询本地任务，把命令取出
        String[] selectColumnName = new String[]{"taskId", "taskType", "command", "lastExecuteTime", "expireFrom",
                "expireTo", "groups", "monitorFrequency", "executeTimeStart", "executeTimeEnd", "isExecute"};
        String slection = "taskType=?";
        String[] condition = new String[]{taskType};
        List<Map<String, String>> list = dbHelper.queryTask(db, tableName, selectColumnName, slection, condition);
        for (Map<String, String> hashmap : list) {
            try {
                Date start = new Date();
                //1 判断是否在有效期内
                String expireFrom = hashmap.get("expireFrom");
                String expireTo = hashmap.get("expireTo");
                if (StringUtils.isNotBlank(expireFrom) && StringUtils.isNotBlank(expireTo)) {
                    long subHourExpireFrom = CommonUtils.getSecondeSub(CommonUtils.YYYY_MM_ddd_HH_mm_ss.parse(expireFrom), start);
                    long subHourExpireTo = CommonUtils.getSecondeSub(CommonUtils.YYYY_MM_ddd_HH_mm_ss.parse(expireTo), start);
                    if (subHourExpireFrom < 0 || subHourExpireTo > 0) {
                        Log.i(TAG, taskType + "有效期已过，不执行");
                        continue;
                    }
                }
                //2 . 判断上一次执行时间是否在频率之内
                String lastExecuteTime = hashmap.get("lastExecuteTime");
                //判断是否首次
                if (StringUtils.isNotBlank(lastExecuteTime)) {
                    Date last = CommonUtils.YYYY_MM_ddd_HH_mm_ss.parse(lastExecuteTime);
                    long subHour = CommonUtils.getHourSub(last, start);
//                    monitorFrequency为空表示执行
                    if (StringUtils.isNotBlank(hashmap.get("monitorFrequency"))) {
                        long monitorFrequency = Long.parseLong(hashmap.get("monitorFrequency"));
                        if (subHour < monitorFrequency) {
                            //频率之内，不执行
                            Log.i(TAG, taskType + "频率之内，不执行");
                            continue;
                        }
                    }
                }
                //取监控计划
                String isExecute = hashmap.get("isExecute");
                String executeTimeStart = hashmap.get("executeTimeStart");
                String executeTimeEnd = hashmap.get("executeTimeEnd");
                if (StringUtils.isNotBlank(isExecute) && StringUtils.isNotBlank(executeTimeStart) && StringUtils.isNotBlank(executeTimeEnd)) {
                    int executeTimeStartLong = Integer.parseInt(executeTimeStart);
                    int executeTimeEndLong = Integer.parseInt(executeTimeEnd);
                    int now = CommonUtils.getHour(start);
                    if (isExecute.equals("1")) {
                        //1 表示在特定时间内执行
                        // executeTimeStartLong <= a < executeTimeEndLong      1:00 <= a < 3:00
                        if (now < executeTimeStartLong || now >= executeTimeEndLong) {
                            Log.i(TAG, taskType + "不在规定时间内，不执行");
                            //不命中范围
                            continue;
                        }
                    } else {
                        // 0 表示在特定的时间内不执行
                        if (now >= executeTimeStartLong && now < executeTimeEndLong + 1) {
                            //命中范围
                            Log.i(TAG, taskType + "在规定时间内，不执行");
                            continue;
                        }
                    }
                }

                //options 与 group节点匹配
                String groups = hashmap.get("groups");
                if (StringUtils.isNotBlank(groups)) {
                    ArrayList<Group> groupArrayList = JSON.parseObject(groups, new TypeReference<ArrayList<Group>>() {
                    });
                    for (Group group : groupArrayList) {

                    }
                }

                //取command命令
                String commad = hashmap.get("command");
                System.out.println(commad);
                PingResponseObject pingResponseObject = new PingResponseObject();
                PageResponseObject pageResponseObject = new PageResponseObject();
                PageResponseObject downloadresponseObject = new PageResponseObject();
                switch (taskType) {
                    case ConstantUtils.PING:
                        pingResponseObject = analysePingCommand(commad);
                        Log.i(TAG, taskType + "执行成功，正在发送数据.....");
                        System.out.println(pingResponseObject);
                        break;
                    case ConstantUtils.PAGE:
                        pageResponseObject = analyseCurlCommand(commad);
                        Log.i(TAG, taskType + "执行成功，正在发送数据.....");
                        System.out.println(pageResponseObject);
                        break;
                    case ConstantUtils.DOWNLOAD:
                        downloadresponseObject = analyseCurlCommand(commad);
                        Log.i(TAG, taskType + "执行成功，正在发送数据.....");
                        System.out.println(downloadresponseObject);
                        break;
                    default:
                        break;
                }

//                执行完成，更新上一次执行时间
                String startString = CommonUtils.YYYY_MM_ddd_HH_mm_ss.format(start);
                ContentValues values = new ContentValues();
                values.put("lastExecuteTime", startString);
                String whereClause = "taskId =? AND taskType =?";
                dbHelper.update(db, tableName, values, whereClause, new String[]{hashmap.get("taskId"), taskType});
                Log.i(TAG, taskType + "更新lastExecuteTime时间.....");
            } catch (ParseException e) {
                Log.e(TAG, "日期转换失败", e);
            } catch (Exception e) {
                Log.e(TAG, "未知错误", e);
            } finally {
                if (db != null) {
                    db.close();
                }
            }
        }

    }

    /**
     * 组装结果
     *
     * @param stringBuffer
     * @param text
     */
    private static void append(StringBuffer stringBuffer, String text) {
        if (stringBuffer != null) {
            stringBuffer.append(text + "\n");
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
            Log.i(TAG, taskType + "exec ping start.");
            process = Runtime.getRuntime().exec(command);
            if (process == null) {
                Log.e(TAG, "  fail:process is null.");
                append(pingResponseObject.getResultBuffer(), " fail:process is null.");
                pingResponseObject.setResult(false);
                return pingResponseObject;
            }
            successReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = successReader.readLine()) != null) {
                //规则解析
                getPingResultByMatchingRules(pingResponseObject, line);
            }
            // 0 表示执行成功，有返回
            int status = process.waitFor();
            if (status == 0) {
                Log.i(TAG, taskType + "exec ping success:");
                append(pingResponseObject.getResultBuffer(), "exec cmd success:" + command);
                pingResponseObject.setResult(true);
            } else {
                Log.e(TAG, "exec ping fail.");
                append(pingResponseObject.getResultBuffer(), "exec cmd fail.");
                pingResponseObject.setResult(false);
            }
            append(pingResponseObject.getResultBuffer(), "exec finished.");

        } catch (IOException e) {
            Log.e(TAG, String.valueOf(e));
        } catch (InterruptedException e) {
            Log.e(TAG, String.valueOf(e));
        } finally {
            Log.i(TAG, taskType + "exec ping exit.");
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

    private PageResponseObject analyseCurlCommand(String command) {
        PageResponseObject pageResponseObject = new PageResponseObject();
        BufferedReader successReader = null;
        pageResponseObject.setResultBuffer(new StringBuffer(""));
        try {
            Log.i(TAG, taskType + "exec curl start.");
            ShellUtils.CommandResult commandResult = new ShellUtils().execCommand(command, false, true);
            if (commandResult.result == -1) {
                Log.i(TAG, taskType + "exec curl fail.");
                append(pageResponseObject.getResultBuffer(), "curl fail --->>>" + command);
                append(pageResponseObject.getResultBuffer(), commandResult.errorMsg);
                pageResponseObject.setResult(false);
                return pageResponseObject;
            }
            Log.i(TAG, taskType + "exec curl success.");
            getCurlResultByMatchingRules(pageResponseObject, commandResult.successMsg);
            append(pageResponseObject.getResultBuffer(), commandResult.successMsg);
            pageResponseObject.setResult(true);

        } catch (Exception e) {
            Log.e(TAG, String.valueOf(e));
        } finally {
            Log.i(TAG, taskType + "exec exit.");
            if (successReader != null) {
                try {
                    successReader.close();
                } catch (IOException e) {
                    Log.e(TAG, String.valueOf(e));
                }
            }
        }
        return pageResponseObject;
    }


    private PingResponseObject getPingResultByMatchingRules(PingResponseObject pingResponseObject, String line) {
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


    private PageResponseObject getCurlResultByMatchingRules(PageResponseObject pageResponseObject, String line) {
        /*
        response_code:          200     0
        content_type:           image/png
        time_namelookup:        0.125
        time_redirect:          0.000
        num_redirects:          0
        time_connect:           0.135
        time_appconnect:        0.252   6
        time_pretransfer:       0.252
        time_starttransfer:     0.329
        time_total:             0.371   9
        size_header:            495
        size_download:          290659
        speed_download:         784443.000
        */
        String[] values = line.split(":");
//        for (int i = 0; i < values.length; i++) {
//            System.out.println(ConstantUtils.PAGE_DOWNLOAD_KEY[i] + ":" + values[i]);
//        }
        BigDecimal bigDecimal1024 = new BigDecimal("1024");
        BigDecimal bigDecimal1000 = new BigDecimal("1000");

        pageResponseObject.setResponseCode(values[0].trim());
        pageResponseObject.setContentType(values[1].trim());
        pageResponseObject.setTimeNamelookup(transformMSValue(values[2], bigDecimal1000)); //DNS耗时
        pageResponseObject.setTimeRedirect(transformMSValue(values[3], bigDecimal1000));
        pageResponseObject.setNumRedirects(values[4].trim());

//        TCP建立连接的耗时 time_connect - time_namelookup   即 values[5].trim() -values[4].trim()
        BigDecimal tcpConnectTime = new BigDecimal(values[5].trim()).subtract(new BigDecimal(values[4].trim())).multiply(bigDecimal1000).setScale(0, BigDecimal.ROUND_HALF_UP);
        pageResponseObject.setTimeTCP(tcpConnectTime);
//        ssl握手耗时  time_appconnect -time_connect
        BigDecimal sslConnetcTime = new BigDecimal(values[6].trim()).subtract(new BigDecimal(values[5].trim())).multiply(bigDecimal1000).setScale(0, BigDecimal.ROUND_HALF_UP);
        pageResponseObject.setTimeSSL(sslConnetcTime);

        //从开始到准备传输的时间
        pageResponseObject.setTimePretransfer(transformMSValue(values[7], bigDecimal1000));
        //首包时间（开始传输时间。在发出请求之后，Web 服务器返回数据的第一个字节所用的时间）
        pageResponseObject.setTimeStarttransfer(transformMSValue(values[8], bigDecimal1000));
        //客户端处理数据时间
        BigDecimal clientTime = new BigDecimal(values[8].trim()).subtract(new BigDecimal(values[7].trim())).multiply(bigDecimal1000).setScale(0, BigDecimal.ROUND_HALF_UP);
        pageResponseObject.setClientTime(clientTime);

        //总时间
        pageResponseObject.setTimeTotal(transformMSValue(values[9].trim(), bigDecimal1000));

        //downloadTime 内容下载时间 totaltime - time_pretransfer
        BigDecimal downloadTime = new BigDecimal(values[9].trim()).subtract(new BigDecimal(values[7].trim())).multiply(bigDecimal1000).setScale(0, BigDecimal.ROUND_HALF_UP);
        pageResponseObject.setDownloadTime(downloadTime);
        BigDecimal siezHeader = new BigDecimal(values[10].trim()).divide(bigDecimal1024, 2, BigDecimal.ROUND_HALF_UP);
        pageResponseObject.setSizeHeader(siezHeader);
        BigDecimal sizeDownload = new BigDecimal(values[11].trim()).divide(bigDecimal1024, 2, BigDecimal.ROUND_HALF_UP);
        pageResponseObject.setSizeDownload(sizeDownload);
        BigDecimal speedDownload = new BigDecimal(values[12].trim()).divide(bigDecimal1024, 2, BigDecimal.ROUND_HALF_UP);
        pageResponseObject.setSpeedDownload(speedDownload);
        return pageResponseObject;
    }

    private BigDecimal transformMSValue(String value, BigDecimal bigDecimal) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return new BigDecimal(value.trim()).multiply(bigDecimal).setScale(0, BigDecimal.ROUND_HALF_UP);
    }

    public static void main(String[] args) {
        String josnString = "[{\n" +
                "\t\"groupCount\": 0,\n" +
                "\t\"idc\": \"ns\",\n" +
                "\t\"isp\": [\"telecom\", \"unicom\"]\n" +
                "}, {\n" +
                "\t\"groupCount\": 0,\n" +
                "\t\"idc\": \"bj\",\n" +
                "\t\"isp\": [\"mobile\"]\n" +
                "}]";
        ArrayList<Group> groupArrayList = JSON.parseObject(josnString, new TypeReference<ArrayList<Group>>() {
        });
        for (Group group : groupArrayList) {
            System.out.println(group);
        }

    }
}
