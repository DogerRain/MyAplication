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
     * 平均延时 单位为 ms ，以下均是 ms
     */
    String avgDelayedTime;
    /**
     * 最小延时
     */
    String minDelayedTime;
    /**
     * 最大延时
     */
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
     * 发送包数，接收包数，丢失率（0~100，表示0%~100%）
     */
    String transmittedPackages;
    String receivedPackages;
    String packetLossRate;

    /**
     * true为执行成功，false为执行失败
     */
    Boolean result;
    /**
     * 结果
     */
    StringBuffer resultBuffer;
}
