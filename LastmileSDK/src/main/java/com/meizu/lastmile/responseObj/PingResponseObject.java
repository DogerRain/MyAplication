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
    String delayedTime;
    String packetLossRate;
}
