package com.meizu.lastmile.requestObj;

import lombok.Data;

/**
 * @Author: huangyongwen
 * @CreateDate: 2020/5/18 17:42
 * @CreateDate:
 */
@Data
public class Instruction {
    /**
     * 名称
     */
    String monitorName ;
    /**
     * 监测类型
     */
    String monitorType;
    /**
     * 监测选项
     */
    String monitorOption;
    /**
     * 有效期
     */
    String validTimeStart;
    String validTimeEnd;
    /**
     * 节点组
     */
    NodeGroup nodeGroup ;
    /**
     * 监测频率
     */
    String monitorFrequency;

    /**
     * 监测计划
     */
    String timeout;


}
