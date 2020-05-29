package com.meizu.lastmile.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @Author: huangyongwen
 * @CreateDate: 2020/5/20 17:03
 * @Description:
 */

public class CommonUtils {


    //没有网络
    public static final int NETWORK_NONE = 0;
    //移动网络
    public static final int NETWORK_MOBILE = 1;
    //无线网络
    public static final int NETWORW_WIFI = 2;

    public static SimpleDateFormat YYYY_MM_ddd_HH_mm_ss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 判断当前网络是否为 Wifi 网络连接
     *
     * @param context
     * @return
     */
    public static boolean WifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    /**
     * 判断当前是否有网络连接
     *
     * @param context
     * @return
     */
    public static boolean NetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable()) {   // 判断网络连接是否打开
            return networkInfo.isConnected();
        }
        return false;
    }

    /**
     * 获取网络方式
     *
     * @param context
     * @return
     */
    public static int getNetWorkStart(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                //连接服务 CONNECTIVITY_SERVICE
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        //网络信息 NetworkInfo
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            //判断是否是wifi
            if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_WIFI)) {
                //返回无线网络
//                Toast.makeText(context, "当前处于无线网络", Toast.LENGTH_SHORT).show();
                return NETWORW_WIFI;
                //判断是否移动网络
            } else if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_MOBILE)) {
//                Toast.makeText(context, "当前处于移动网络", Toast.LENGTH_SHORT).show();
                //返回移动网络
                return NETWORK_MOBILE;
            }
        } else {
            //没有网络
//            Toast.makeText(context, "当前没有网络", Toast.LENGTH_SHORT).show();
            return NETWORK_NONE;
        }
        //默认返回  没有网络
        return NETWORK_NONE;
    }

    /**
     * String 转 Date
     *
     * @param strDate
     * @return
     */
    public static Date strToDateLong(String strDate) {
        if (StringUtils.isBlank(strDate)) {
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    public static long getHourSub(Date startDate, Date endDate) {
        return (endDate.getTime() - startDate.getTime()) / ( 1 * 60 * 60 * 1000);
    }
    public static long getMinuteSub(Date startDate, Date endDate) {
        //返回分钟
        return (endDate.getTime() - startDate.getTime()) / (1 * 1 * 60 * 1000);
    }
    public static long getSecondeSub(Date startDate, Date endDate) {
        //返回秒
        return (endDate.getTime() - startDate.getTime()) / (1 * 1 * 1 * 1000);
    }


    public static int getHour(String strDate) {
        Calendar calendar = Calendar.getInstance();
        try {
            Date date = YYYY_MM_ddd_HH_mm_ss.parse(strDate);
            calendar.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public static int getHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public static void main(String[] args) {
        try {
            Date date = YYYY_MM_ddd_HH_mm_ss.parse("2020-05-26 16:11:30");
            Date date1 =  YYYY_MM_ddd_HH_mm_ss.parse("2020-05-26 16:11:20");
            Date date3 =  YYYY_MM_ddd_HH_mm_ss.parse("2020-05-26 00:11:20");
            System.out.println(getHourSub(date,date1));
            System.out.println(getSecondeSub(date,date1));
            System.out.println(getHour(date3));
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        System.out.println(getHour(strDate));

    }

}
