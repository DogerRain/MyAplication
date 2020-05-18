package com.meizu.lastmile.service;

import android.util.Log;

import com.meizu.lastmile.requestObj.Ping.PingRequestObject;
import com.meizu.lastmile.responseObj.PingResponseObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @Author： Administrator
 * @CreateDate： 2020/5/18 21:12
 * @Descriotion：
 */

public class PingService {
    public PingResponseObject getPingInfo(PingRequestObject pingRequestObject) {
        PingRequestObject pingRequestObject1 = PingRequestObject.builder().ip("").build();

        return null;
    }

    public static void main(String[] args) throws IOException, InterruptedException {

    }
}
