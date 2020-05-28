package com.meizu.lastmile.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.meizu.lastmile.Utils.CommonUtils;
import com.meizu.lastmile.Utils.ConstantUtils;
import com.meizu.lastmile.Utils.DatabaseHelper;
import com.meizu.lastmile.responseObj.PageResponseObject;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
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
    private String TAG = "LastMileSDK》》》 PageRunLocalTaskService";

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
        if (!dbHelper.isTableExist(db, ConstantUtils.T_TASK)) {
            //不存在证明用户可能清除了数据

            return;
        }
        //4.查询本地任务，把命令取出
        String[] selectColumnName = new String[]{"taskId", "taskType", "command", "lastExecuteTime", "expireFrom", "expireTo", "groups", "monitorFrequency", "executeTimeStart", "executeTimeEnd", "isExecute"};
        List<Map<String, String>> list = dbHelper.queryTask(db, ConstantUtils.T_TASK, selectColumnName, null, null);
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
                String isExecute = hashmap.get("isExecute");

                //取command命令
                String commad = hashmap.get("command") + "";
                //解析命令

                PageResponseObject pageResponseObject = analyseCurlCommand(commad);
                System.out.println(pageResponseObject);
                //上传到大数据那边
//                pingResponseObjece


//                执行完成，更新上一次执行时间
                String startString = formatter.format(start);
                ContentValues values = new ContentValues();
                values.put("lastExecuteTime", startString);
                String whereClause = "taskId =? AND taskType =?";
                dbHelper.update(db, ConstantUtils.T_TASK, values, whereClause, new String[]{hashmap.get("taskId") + "", hashmap.get("taskType") + ""});
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

    private PageResponseObject analyseCurlCommand(String command) {
        String line = null;
        Process process = null;
        BufferedReader successReader = null;
        StringBuffer resultStringBuffer = new StringBuffer("");
        PageResponseObject pageResponseObject = new PageResponseObject();
        pageResponseObject.setResultBuffer(resultStringBuffer);
        try {
            process = Runtime.getRuntime().exec(command);
            if (process == null) {
                Log.e(TAG, " curl fail:process is null.");
                append(pageResponseObject.getResultBuffer(), "curl fail:process is null.");
                pageResponseObject.setResult(false);
                return pageResponseObject;
            }

            successReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = successReader.readLine()) != null) {
                //规则解析
                getCurlResultByMatchingRules(pageResponseObject, line);


            }
            // 0 表示执行成功，有返回
            int status = process.waitFor();
            if (status == 0) {
                Log.i(TAG, "exec cmd success:" + command);
                append(pageResponseObject.getResultBuffer(), "exec cmd success:" + command);
                pageResponseObject.setResult(true);
            } else {
                Log.e(TAG, "exec cmd fail.");
                append(pageResponseObject.getResultBuffer(), "exec cmd fail.");
                pageResponseObject.setResult(false);
            }
            Log.i(TAG, "exec finished.");
            append(pageResponseObject.getResultBuffer(), "exec finished.");

        } catch (IOException e) {
            Log.e(TAG, String.valueOf(e));
        } catch (InterruptedException e) {
            Log.e(TAG, String.valueOf(e));
        } finally {
            Log.i(TAG, "curl exit.");
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
        return pageResponseObject;
    }

    /**
     * 组装curl结果
     *
     * @param stringBuffer
     * @param text
     */
    private static void append(StringBuffer stringBuffer, String text) {
        if (stringBuffer != null) {
            stringBuffer.append(text + "\n");
        }
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
        for (int i = 0; i < values.length; i++) {
            System.out.println(ConstantUtils.PAGE_DOWNLOAD_KEY[i] + ":" + values[i]);
        }
        BigDecimal bigDecimal1024 = new BigDecimal("1024");
        BigDecimal bigDecimal1000 = new BigDecimal("1000");

        pageResponseObject.setResponseCode(values[0].trim());
        pageResponseObject.setContentType(values[1].trim());
        pageResponseObject.setTimeNamelookup(transformMSValue(values[2], bigDecimal1000)); //DNS耗时
        pageResponseObject.setTimeRedirect(transformMSValue(values[3], bigDecimal1000));
        pageResponseObject.setNumRedirects(values[4].trim());

//        TCP建立连接的耗时 time_connect - time_namelookup   即 values[5].trim() -values[4].trim()
        BigDecimal tcpConnectTime = new BigDecimal(values[5].trim()).subtract(new BigDecimal(values[4].trim())).multiply(bigDecimal1000).setScale(0);
        pageResponseObject.setTimeTCP(tcpConnectTime);
//        ssl握手耗时  time_appconnect -time_connect
        BigDecimal sslConnetcTime = new BigDecimal(values[6].trim()).subtract(new BigDecimal(values[5].trim())).multiply(bigDecimal1000).setScale(0);
        pageResponseObject.setTimeSSL(sslConnetcTime);

        //从开始到准备传输的时间
        pageResponseObject.setTimePretransfer(transformMSValue(values[7], bigDecimal1000));
        //首包时间（开始传输时间。在发出请求之后，Web 服务器返回数据的第一个字节所用的时间）
        pageResponseObject.setTimeStarttransfer(transformMSValue(values[8], bigDecimal1000));
        //客户端处理数据时间
        BigDecimal clientTime = new BigDecimal(values[8].trim()).subtract(new BigDecimal(values[7].trim())).multiply(bigDecimal1000).setScale(0);
        pageResponseObject.setClientTime(clientTime);

        //总时间
        pageResponseObject.setTimeTotal(transformMSValue(values[9].trim(), bigDecimal1000));

        //downloadTime 内容下载时间 totaltime - time_pretransfer
        BigDecimal downloadTime = new BigDecimal(values[9].trim()).subtract(new BigDecimal(values[7].trim())).multiply(bigDecimal1000).setScale(0);
        pageResponseObject.setDownloadTime(downloadTime);
        BigDecimal siezHeader = new BigDecimal(values[10].trim()).divide(bigDecimal1024, 2, BigDecimal.ROUND_HALF_UP);
        pageResponseObject.setSizeHeader(siezHeader);
        BigDecimal sizeDownload = new BigDecimal(values[11].trim()).divide(bigDecimal1024, 2, BigDecimal.ROUND_HALF_UP);
        pageResponseObject.setSizeDownload(sizeDownload);
        BigDecimal speedDownload = new BigDecimal(values[12].trim()).divide(bigDecimal1024, 2, BigDecimal.ROUND_HALF_UP);
        pageResponseObject.setSpeedDownload(speedDownload);

        pageResponseObject.setResult(true);
        return pageResponseObject;

        /*if (line.contains("response_code")) {
            String responseCode = line.split(":")[1].trim();
            pageResponseObject.setResponseCode(responseCode);
        }
        if (line.contains("content_type")) {
            String contentType = line.split(":")[1].trim();
            pageResponseObject.setContentType(contentType);
        }
        if (line.contains("time_namelookup")) {
            String values = line.split(":")[1].trim();
            pageResponseObject.setTimeNamelookup(values);
        }
        if (line.contains("time_redirect")) {
            String values = line.split(":")[1].trim();
            pageResponseObject.setTimeNamelookup(values);
        }
        if (line.contains("time_connect")) {
            String values = line.split(":")[1].trim();
            pageResponseObject.setTimeNamelookup(values);
        }
        if (line.contains("time_appconnect")) {
            String values = line.split(":")[1].trim();
            pageResponseObject.setTimeNamelookup(values);
        }
        if (line.contains("time_pretransfer")) {
            String values = line.split(":")[1].trim();
            pageResponseObject.setTimeNamelookup(values);
        }
        if (line.contains("time_starttransfer")) {
            String values = line.split(":")[1].trim();
            pageResponseObject.setTimeNamelookup(values);
        }
        if (line.contains("time_total")) {
            String values = line.split(":")[1].trim();
            pageResponseObject.setTimeNamelookup(values);
        }
        if (line.contains("size_header")) {
            String values = line.split(":")[1].trim();
            pageResponseObject.setTimeNamelookup(values);
        }
        if (line.contains("size_download")) {
            String values = line.split(":")[1].trim();
            pageResponseObject.setTimeNamelookup(values);
        }
        if (line.contains("speed_download")) {
            String values = line.split(":")[1].trim();
            pageResponseObject.setTimeNamelookup(values);
        }*/


    }

    public BigDecimal transformMSValue(String value, BigDecimal bigDecimal) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return new BigDecimal(value.trim()).multiply(bigDecimal).setScale(0);
    }

    public static void main(String[] args) throws ParseException {
        BigDecimal bd = new BigDecimal(495);
        BigDecimal a = bd.divide(new BigDecimal("1024"), 4, BigDecimal.ROUND_HALF_UP);
        BigDecimal b = new BigDecimal("0.371").subtract(new BigDecimal("0.329")).multiply(new BigDecimal("1000")).setScale(0);
        System.out.println(a);
        System.out.println(b);


    }
}
