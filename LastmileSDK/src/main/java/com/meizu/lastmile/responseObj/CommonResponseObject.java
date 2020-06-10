package com.meizu.lastmile.responseObj;

import lombok.Data;

/**
 * @Author: huangyongwen
 * @CreateDate: 2020/6/4 14:43
 * @Description:
 */
@Data
public class CommonResponseObject  {
    String taskId;
    String taskName;
    String taskType;
    /**
     * 执行命令时间
     */
    String excuseCommandTime;

    /**
     * true为执行成功，false为执行失败
     */
    Boolean result;

    /**
     * 结果
     */
    StringBuffer resultBuffer;

}
