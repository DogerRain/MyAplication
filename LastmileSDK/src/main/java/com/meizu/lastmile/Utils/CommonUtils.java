package com.meizu.lastmile.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
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
                Toast.makeText(context, "当前处于无线网络", Toast.LENGTH_SHORT).show();
                return NETWORW_WIFI;
                //判断是否移动网络
            } else if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_MOBILE)) {
                Toast.makeText(context, "当前处于移动网络", Toast.LENGTH_SHORT).show();
                //返回移动网络
                return NETWORK_MOBILE;
            }
        } else {
            //没有网络
            Toast.makeText(context, "当前没有网络", Toast.LENGTH_SHORT).show();
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
        long hour = 0;
        hour = (endDate.getTime() - startDate.getTime()) / (1 * 24 * 60 * 60 * 1000);
        return hour;
    }



}
