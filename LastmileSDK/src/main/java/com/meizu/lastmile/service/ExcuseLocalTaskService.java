package com.meizu.lastmile.service;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.meizu.lastmile.Utils.CommonUtils;
import com.meizu.lastmile.Utils.DatabaseHelper;
import com.meizu.lastmile.Utils.ShellUtils;
import com.meizu.lastmile.constants.ConstantUtils;
import com.meizu.lastmile.requestObj.Group;
import com.meizu.lastmile.requestObj.Options;
import com.meizu.lastmile.responseObj.PageResponseObject;
import com.meizu.lastmile.responseObj.PingResponseObject;
import com.meizu.statsapp.v3.PkgType;

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

public class ExcuseLocalTaskService {

    private String TAG = Thread.currentThread().getName() + "--->>>LastMileSDK--->>> ExcuseLocalTaskService";


    private Context context;
    private String taskType;
    private String tableName;
    private Options options;

    private String eventName;
    private String pageName;
    PkgType pkgType;
    String key;

    public ExcuseLocalTaskService(Context context, String taskType, String tableName, Options options, String eventName, String pageName, PkgType pkgType, String key) {
        this.context = context;
        this.taskType = taskType;
        this.tableName = tableName;
        this.options = options;
        this.eventName = eventName;
        this.pageName = pageName;
        this.pkgType = pkgType;
        this.key = key;
    }

    public void excuseLocalTask() {
        //1. 获取本地数据库
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        //2. 取得一个的数据库对象
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        //3 判断表是否存在
        if (!dbHelper.isTableExist(db, tableName)) {
            //不存在证明用户可能清除了数据
            Log.i(TAG, "表不存在，退出。。。");
            return;
        }
        Log.i(TAG, "当前任务：" + taskType);
        //4.查询本地任务，把命令取出
        String[] selectColumnName = new String[]{"taskId", "taskType","taskName", "command", "lastExecuteTime", "expireFrom",
                "expireTo", "groups", "monitorFrequency", "executeTimeStart", "executeTimeEnd", "isExecute", "status"};
        String slection = "taskType=?";
        String[] condition = new String[]{taskType};
        List<Map<String, String>> list = dbHelper.queryTask(db, tableName, selectColumnName, slection, condition);
        for (Map<String, String> hashmap : list) {
            try {
                if (!isExcuse(hashmap)) {
                    continue;
                }

                //options 与 group节点匹配
//                String groups = hashmap.get("groups");
//                if (StringUtils.isNotBlank(groups)) {
//                    ArrayList<Group> groupArrayList = JSON.parseObject(groups, new TypeReference<ArrayList<Group>>() {
//                    });
//                    for (Group group : groupArrayList) {
//
//                    }
//                }
                excuseTaskAndReport(hashmap);

//                执行完成，更新上一次执行时间
                String startString = CommonUtils.YYYY_MM_ddd_HH_mm_ss.format(new Date());
                ContentValues values = new ContentValues();
                values.put("lastExecuteTime", startString);
                String whereClause = "taskId =? AND taskType =?";
                dbHelper.update(db, tableName, values, whereClause, new String[]{hashmap.get("taskId"), taskType});
                Log.i(TAG, taskType + "更新lastExecuteTime时间.....");
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
     * @Author huangyongwen
     * @CreateDate 2020/6/3 17:25
     * @params
     * @Description 根据规则是否执行
     */
    private Boolean isExcuse(Map<String, String> hashmap) {
        try {
            String status = hashmap.get("status");
            if (status.equals("0")) {
                return false;
            }
            Date start = new Date();
            //1 判断是否在有效期内
            String expireFrom = hashmap.get("expireFrom");
            String expireTo = hashmap.get("expireTo");
            if (StringUtils.isNotBlank(expireFrom) && StringUtils.isNotBlank(expireTo)) {
                long subHourExpireFrom = CommonUtils.getSecondeSub(CommonUtils.YYYY_MM_ddd_HH_mm_ss.parse(expireFrom), start);
                long subHourExpireTo = CommonUtils.getSecondeSub(CommonUtils.YYYY_MM_ddd_HH_mm_ss.parse(expireTo), start);
                if (subHourExpireFrom < 0 || subHourExpireTo > 0) {
                    Log.i(TAG, taskType + "有效期已过，不执行");
                    return false;
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
                        return false;
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
                        return false;
                    }
                } else {
                    // 0 表示在特定的时间内不执行
                    if (now >= executeTimeStartLong && now < executeTimeEndLong + 1) {
                        //命中范围
                        Log.i(TAG, taskType + "在规定时间内，不执行");
                        return false;
                    }
                }
            }
        } catch (ParseException e) {
            Log.e(TAG, "日期转换失败", e);
            return false;
        }
        return true;
    }

    /**
     * @Author huangyongwen
     * @CreateDate 2020/6/3 17:26
     * @params
     * @Description 执行命令并且上传到大数据
     */
    private void excuseTaskAndReport(Map<String, String> hashmap) {
        //取command命令
        String commad = hashmap.get("command");
        ReportToNomalService reportToNomalService = new ReportToNomalService((Application) context.getApplicationContext(), pkgType, key);
        switch (taskType) {
            case ConstantUtils.PING:
                PingResponseObject pingResponseObject = analysePingCommand(commad);
                pingResponseObject.setTaskId(hashmap.get("taskId"));
                pingResponseObject.setTaskType(hashmap.get("taskType"));
                pingResponseObject.setTaskName(hashmap.get("taskName"));
                Log.i(TAG, taskType + "执行成功，正在发送数据.....");
                reportToNomalService.reportDataToNomal(eventName, pageName, pingResponseObject, options);
                Log.i(TAG, "打印Objetc:" + pingResponseObject);
                break;
            case ConstantUtils.PAGE:
                PageResponseObject pageResponseObject = analyseCurlCommand(commad);
                pageResponseObject.setTaskId(hashmap.get("taskId"));
                pageResponseObject.setTaskType(hashmap.get("taskType"));
                pageResponseObject.setTaskName(hashmap.get("taskName"));
                Log.i(TAG, taskType + "执行成功，正在发送数据.....");
                reportToNomalService.reportDataToNomal(eventName, pageName, pageResponseObject, options);
                Log.i(TAG, "打印Objetc:" + pageResponseObject);
                break;
            case ConstantUtils.DOWNLOAD:
                PageResponseObject downloadresponseObject = analyseCurlCommand(commad);
                downloadresponseObject.setTaskId(hashmap.get("taskId"));
                downloadresponseObject.setTaskType(hashmap.get("taskType"));
                downloadresponseObject.setTaskName(hashmap.get("taskName"));
                Log.i(TAG, taskType + "执行成功，正在发送数据.....");
                reportToNomalService.reportDataToNomal(eventName, pageName, downloadresponseObject, options);
                Log.i(TAG, "打印Objetc:" + downloadresponseObject);
                break;
            default:
                break;
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

    /**
     * @Author huangyongwen
     * @CreateDate 2020/6/2 10:13
     * @params command
     * @Description 解析ping的结果
     */
    private PingResponseObject analysePingCommand(String command) {
        String line = null;
        Process process = null;
        BufferedReader successReader = null;
        StringBuffer resultStringBuffer = new StringBuffer("command--->>>\n");
        PingResponseObject pingResponseObject = new PingResponseObject();
        try {
            Log.i(TAG, taskType + "exec ping start.");
            process = Runtime.getRuntime().exec(command);
            //执行命令
            append(resultStringBuffer, command);
            append(resultStringBuffer,"result---->>>");
            if (process == null) {
                Log.e(TAG, "  fail:process is null.");
                append(resultStringBuffer, " fail:process is null.");
                pingResponseObject.setResultBuffer(resultStringBuffer);
                pingResponseObject.setResult(false);
                return pingResponseObject;
            }
            successReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = successReader.readLine()) != null) {
                //规则解析
                getPingResultByMatchingRules(pingResponseObject, line);
                append(resultStringBuffer,line);
            }
            // 0 表示执行成功，有返回
            int status = process.waitFor();
            if (status == 0) {
                Log.i(TAG, taskType + "exec ping success:");
                append(resultStringBuffer, "exec ping success");
                pingResponseObject.setResult(true);
            } else {
                Log.e(TAG, "exec ping fail.");
                append(resultStringBuffer, "exec cmd fail.");
                pingResponseObject.setResult(false);
            }
            pingResponseObject.setResultBuffer(resultStringBuffer);
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

    /**
     * @Author huangyongwen
     * @CreateDate 2020/6/2 10:14
     * @params command
     * @Description 解析curl的结果
     */
    private PageResponseObject analyseCurlCommand(String command) {
        PageResponseObject pageResponseObject = new PageResponseObject();
        BufferedReader successReader = null;
        StringBuffer resultStringBuffer = new StringBuffer("command--->>>\n");
        //执行命令
        append(resultStringBuffer, command);
        append(resultStringBuffer,"result---->>>");
        try {
            Log.i(TAG, taskType + " exec curl start.");
            ShellUtils.CommandResult commandResult = new ShellUtils().execCommand(command, false, true);
            if (commandResult.result == -1) {
                Log.i(TAG, taskType + " exec curl fail.");
                append(resultStringBuffer, " curl exec fail ");
                append(resultStringBuffer, commandResult.errorMsg);
                pageResponseObject.setResultBuffer(resultStringBuffer);
                pageResponseObject.setResult(false);
                return pageResponseObject;
            }
            Log.i(TAG, taskType + " exec curl success.");
            append(resultStringBuffer,commandResult.successMsg);
            append(resultStringBuffer," exec curl success.");
            pageResponseObject.setResultBuffer(resultStringBuffer);
            getCurlResultByMatchingRules(pageResponseObject, commandResult.successMsg);
            pageResponseObject.setResult(true);
        } catch (Exception e) {
            Log.e(TAG, String.valueOf(e));
        } finally {
            Log.i(TAG, taskType + " exec curl exit.");
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
        time_connect:           0.135   5
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
//        ssl握手耗时  time_appconnect -time_connect, 如果time_appconnect = 0 ,表示使用http
        if (new BigDecimal(values[6].trim()).compareTo(BigDecimal.ZERO) == 0) {
            pageResponseObject.setTimeSSL(BigDecimal.ZERO);
        } else {
            BigDecimal sslConnetcTime = new BigDecimal(values[6].trim()).subtract(new BigDecimal(values[5].trim())).multiply(bigDecimal1000).setScale(0, BigDecimal.ROUND_HALF_UP);
            pageResponseObject.setTimeSSL(sslConnetcTime);
        }

        //从开始到准备传输的时间
        pageResponseObject.setTimePretransfer(transformMSValue(values[7], bigDecimal1000));
        //首包时间（开始传输时间。在发出请求之后，Web 服务器返回数据的第一个字节所用的时间）
        pageResponseObject.setTimeStarttransfer(transformMSValue(values[8], bigDecimal1000));
        //客户端处理数据时间 time_starttransfer-time_pretransfer，
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
        PageResponseObject pageResponseObject = new PageResponseObject();
        if (new BigDecimal("000.000".trim()).compareTo(BigDecimal.ZERO) == 0) {
            pageResponseObject.setTimeSSL(BigDecimal.ZERO);
            System.out.println("确实是0000");
        }
        System.out.println(pageResponseObject.getTimeSSL());

        StringBuffer stringBuffer  = new StringBuffer("1111");
        append(stringBuffer,"--->>>123");
        append(stringBuffer,"--->>>123");
        System.out.println(stringBuffer);
    }
}
