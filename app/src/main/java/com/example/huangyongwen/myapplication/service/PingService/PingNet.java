package com.example.huangyongwen.myapplication.service.PingService;

/**
 * @Author： Administrator
 * @CreateDate： 2020/5/18 23:15
 * @Descriotion：
 */

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PingNet extends Thread{
    private static final String TAG = "PingNet";

    @Override
    public void run() {
        PingNetEntity pingNetEntity=new PingNetEntity("www.baidu.com",3,5,new StringBuffer());
        pingNetEntity=PingNet.ping(pingNetEntity);
//        Log.i("testPing",pingNetEntity.getIp());
//        Log.i("testPing","time="+pingNetEntity.getPingTime());
//        Log.i("testPing",pingNetEntity.isResult()+"");
    }


    /**
     * @param pingNetEntity 检测网络实体类
     * @return 检测后的数据
     */
    public static PingNetEntity ping(PingNetEntity pingNetEntity) {
        String line = null;
        Process process = null;
        BufferedReader successReader = null;
        String command = "ping -c " + pingNetEntity.getPingCount() + " -w " + pingNetEntity.getPingWtime() + " " + pingNetEntity.getIp();
//        String command = " curl -o /dev/null -s -w time_namelookup:\"\\t\"%{time_namelookup}\"\\n\"time_connect:\"\\t\\t\"%{time_connect}\"\\n\"time_appconnect:\"\\t\"%{time_appconnect}\"\\n\"time_pretransfer:\"\\t\"%{time_pretransfer}\"\\n\"time_starttransfer:\"\\t\"%{time_starttransfer}\"\\n\"time_total:\"\\t\\t\"%{time_total}\"\\n\"time_redirect:\"\\t\\t\"%{time_redirect}\"\\n\" https://rain.baimuxym.cn/";
//        String command = "ping -c " + pingCount + " " + host;
        System.out.println(command);
        try {
            process = Runtime.getRuntime().exec(command);
            if (process == null) {
                Log.e(TAG, "ping fail:process is null.");
                append(pingNetEntity.getResultBuffer(), "ping fail:process is null.");
                pingNetEntity.setPingTime(null);
                pingNetEntity.setResult(false);
                return pingNetEntity;
            }
            successReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = successReader.readLine()) != null) {
                Log.i(TAG, line);
                append(pingNetEntity.getResultBuffer(), line);
                String time;
                if ((time = getTime(line)) != null) {
                    pingNetEntity.setPingTime(time);
                }
            }
            int status = process.waitFor();
            if (status == 0) {
                Log.i(TAG, "exec cmd success:" + command);
                append(pingNetEntity.getResultBuffer(), "exec cmd success:" + command);
                pingNetEntity.setResult(true);
            } else {
                Log.e(TAG, "exec cmd fail.");
                append(pingNetEntity.getResultBuffer(), "exec cmd fail.");
                pingNetEntity.setPingTime(null);
                pingNetEntity.setResult(false);
            }
            Log.i(TAG, "exec finished.");
            append(pingNetEntity.getResultBuffer(), "exec finished.");
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
        Log.i(TAG, pingNetEntity.getResultBuffer().toString());
        return pingNetEntity;
    }

    private static void append(StringBuffer stringBuffer, String text) {
        if (stringBuffer != null) {
            stringBuffer.append(text + "\n");
        }
    }

    private static String getTime(String line) {
        String[] lines = line.split("\n");
        String time = null;
        for (String l : lines) {
            if (!l.contains("time="))
                continue;
            int index = l.indexOf("time=");
            time = l.substring(index + "time=".length());
            Log.i(TAG, time);
        }
        return time;
    }

}
