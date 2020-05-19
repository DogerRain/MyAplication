package com.meizu.lastmile.responseObj;

import lombok.Builder;
import lombok.Data;

/**
 * @Author: huangyongwen
 * @CreateDate: 2020/5/18 18:15
 * @CreateDate:
 */
@Data
@Builder
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
    String packetLossRate;
}
