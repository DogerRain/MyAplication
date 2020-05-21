package com.meizu.lastmile.requestObj;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Author: huangyongwen
 * @CreateDate: 2020/5/21 11:00
 * @Description:
 */
@Builder
@Data
@ToString(callSuper = true)
/**
 * 附带ping操作
 */
public class Ping {

    /**
     * 主机
     */
    String host;



    /**
     * 什么时候触发ping，"always" 总是执行、 "error" 页面出现错误时执行、 "disabled" 不执行
     */
    String trigger;

    /**
     * Ping包大小，单位 byte
     */
    int size ;

    /**
     * Ping次数
     */
    boolean tcpPing ;

    /**
     * 间隔
     */

    double interval;

    /**
     * ipv6: 0 只支持 ipv4, 1 只支持 ipv6, 2 IPV4/IPV6混合节点
     */

    int supportIPv6;

    /**
     * DNS协议与节点协议是否一致， 0 自动， 1 一致
     */

    int dnsMatch;

}
