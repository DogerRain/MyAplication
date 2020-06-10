package com.meizu.lastmile.responseObj;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: huangyongwen
 * @CreateDate: 2020/5/18 18:15
 * @CreateDate:
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PingResponseObject extends CommonResponseObject{

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




}
