package com.meizu.lastmile.requestObj.Ping;

import com.meizu.lastmile.requestObj.Instruction;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Author: huangyongwen
 * @CreateDate: 2020/5/18 17:49
 * @CreateDate:
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PingRequestObject extends Instruction{

    String pingTaskId;

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







    /**
     * 上一次执行时间
     */
    String lastExecuteTime;

    /**
     * 执行频率，单位 小时
     */
//    String frequency;


    /**
     * 监测计划
     */




}
