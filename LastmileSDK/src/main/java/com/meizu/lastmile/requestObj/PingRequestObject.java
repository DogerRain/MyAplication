package com.meizu.lastmile.requestObj;

import lombok.Data;

/**
 * @Author: huangyongwen
 * @CreateDate: 2020/5/18 17:49
 * @CreateDate:
 */
@Data
public class PingRequestObject extends Instruction {

    /**
     * 发送数据包次数 默认4次
     */
    int count = 4;

    /**
     * host ip
     */

    String host;
    /**
     * 域名
     */
    String hostName;
    /**
     * 什么时候触发ping，"always" 总是执行、 "error" 页面出现错误时执行、 "disabled" 不执行
     */
    String trigger = "always";

    /**
     * Ping包大小，单位 byte
     */
    int size = 64;

    /**
     * Ping次数
     */
    Boolean tcpPing = false;

    /**
     * 超时时间
     */
    int timeout = 5;


    /**
     * 间隔
     */

    double interval = 1;

    /**
     * ipv6: 0 只支持 ipv4, 1 只支持 ipv6, 2 IPV4/IPV6混合节点
     */

    int supportIPv6;

    /**
     * DNS协议与节点协议是否一致， 0 自动， 1 一致
     */

    int dnsMatch;

    /**
     * 上一次执行时间
     */
    String lastExecuteTime;


    /**
     * 监测频率,单位小时
     */
    String monitorFrequency;


}
