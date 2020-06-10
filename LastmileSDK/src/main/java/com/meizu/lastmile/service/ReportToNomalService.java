package com.meizu.lastmile.service;

import android.app.Application;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.meizu.lastmile.Utils.CommonUtils;
import com.meizu.lastmile.constants.ConstantUtils;
import com.meizu.lastmile.requestObj.Options;
import com.meizu.lastmile.responseObj.PageResponseObject;
import com.meizu.lastmile.responseObj.PingResponseObject;
import com.meizu.statsapp.v3.PkgType;
import com.meizu.statsapp.v3.UsageStatsProxy3;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author huangyongwen
 * @CreateDate 2020/5/26 14:18
 * @Description 上报到大数据
 */
public class ReportToNomalService {
    private String TAG = Thread.currentThread().getName() + "--->>>LastMileSDK--->>> ReportToNomalService";
    Application application;
    PkgType pkgType;
    String pkgKey;
    public ReportToNomalService(Application application, PkgType pkgType, String pkgKey) {
        this.application = application;
        this.pkgType = pkgType;
        this.pkgKey = pkgKey;
    }


    /**
     * @param eventName 事件名称，非空
     * @param pageName  事件发生的页面，可以为空
     * @param object    对象
     */
    public void reportDataToNomal(String eventName, String pageName, Object object, Options options) {
        if (object == null) {
            return;
        }
        try {
            UsageStatsProxy3.init(application, pkgType, pkgKey);
            UsageStatsProxy3 usageStatsProxy3 = UsageStatsProxy3.getInstance();
            Map<String, String> map1 = CommonUtils.jsonObjectToMap(object);
            Map<String, String> map2 = CommonUtils.jsonObjectToMap(options);
            Map<String, String> combineResultMap = new HashMap<String, String>();
            combineResultMap.putAll(map1);
            combineResultMap.putAll(map2);
//            System.out.println(combineResultMap);
            if (ConstantUtils.SWITCH){
//                usageStatsProxy3.onEvent(eventName, pageName, combineResultMap);
                usageStatsProxy3.onEventRealtime(eventName, pageName, combineResultMap);
//                usageStatsProxy3.onEventNeartime(eventName, pageName, combineResultMap);
                Log.i(TAG, "发送数据到nomal成功");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void main(String[] args) throws Exception {
        PageResponseObject pageResponseObject = new PageResponseObject();
        PingResponseObject pingResponseObject = new PingResponseObject();

        if (pageResponseObject == null) {
            System.out.println("pageResponseObject is null ");
        }

        String rest = JSON.toJSONString(pageResponseObject, SerializerFeature.WriteMapNullValue);
        String rest1 = JSON.toJSONString(pingResponseObject, SerializerFeature.WriteMapNullValue);
        System.out.println(rest);
        System.out.println(rest1);
//
//        System.out.println(CommonUtils.objectToMap(pageResponseObject));


    }

}
