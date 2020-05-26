package com.meizu.lastmile.requestObj;

import java.util.List;

import lombok.Data;

/**
 * @Author: huangyongwen
 * @CreateDate: 2020/5/18 17:42
 * @CreateDate: 请求公共参数
 */
@Data
public class Instruction {
    /**
     * 任务Id
     */
    String taskId;

    /**
     * 监测类型
     */
    String taskType;

    /**
     * 名称
     */
    String taskName;

    /**
     * 节点组
     */
    List<Group> groups;



    //额外参数，暂时不写



    /**
     * 监测计划，设置监测频率的同时亦可设定监测计划，
     * 如：周六-周⼀00:00:00 – 20:00:00 不执⾏/执行
     */
    String executeTimeStart;
    String executeTimeEnd;
    /**
     * 是否在执行计划执行，true表示仅在执行计划执行，false表示 在执行计划 外执行
     */
    Boolean isExecute;



    /**
     * 有效期
     */
    String expireFrom;
    String expireTo;


}
