package com.meizu.lastmile.requestObj.Ping;

import com.meizu.lastmile.requestObj.Instruction;

import lombok.Builder;
import lombok.Data;

/**
 * @Author: huangyongwen
 * @CreateDate: 2020/5/18 17:49
 * @CreateDate:
 */
@Data
@Builder
public class PingRequestObject extends Instruction{
    /**
     * 超时时间 默认 1000ms
     */
    String timeout;
    /**
     * 发送数据包次数 默认4次
     */
    String count ;
    /**
     * 指定数据包大小，默认 64Byte
     */
    String packageSize;
    String ip;
    String hostName;
}
