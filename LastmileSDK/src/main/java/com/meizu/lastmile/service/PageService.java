package com.meizu.lastmile.service;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.meizu.lastmile.requestObj.PageRequestObject;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @Author： huangyongwen
 * @CreateDate： 2020/5/18 21:12
 * @Descriotion：接收任务，并存储在本地
 */

public class PageService extends Thread {

    private String TAG = "LastMileSDK》》》 PageService";

    private Context context;
    String jsonString;

    public PageService(String pingJsonString, Context context) {
        this.context = context;
        this.jsonString = pingJsonString;
    }

    @Override
    public void run() {
        receiveInstructionAndStorage();
    }

    public void receiveInstructionAndStorage() {


        if (StringUtils.isBlank(jsonString)) {
            return;
        }

        PageRequestObject pageRequestObject = JSON.parseObject(jsonString, PageRequestObject.class);



    }

    public static void main(String[] args) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url("https://fms.res.meizu.com/dms/2020/05/08/041087f7-680e-40fe-a2cc-bcdb81931aa3.png")
                .method("GET", null)
                .addHeader("User-Agent", "mz-lastmile")
                .build();
        try {
            Response response = client.newCall(request).execute();
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
