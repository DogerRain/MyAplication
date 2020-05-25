package com.meizu.lastmile.responseObj;

import lombok.Data;

/**
 * @Author: huangyongwen
 * @CreateDate: 2020/5/18 18:15
 * @CreateDate:
 */
@Data
public class PingResponseObject {
    /**
     * 平均延时
     */
    String avgDelayedTime;
    String minDelayedTime;
    String maxDelayedTime;
    /**
     * 平均偏差
     */
    String mdevDelayedTime;
    /**
     * 发送耗时
     */
    String sendUsedTime;



    /**
     * 发送包，接收包，丢失率
     */
    String transmittedPackages;
    String receivedPackages;
    String packetLossRate;

    Boolean result;
    StringBuffer resultBuffer;

}
