package com.meizu.lastmile.requestObj;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: huangyongwen
 * @CreateDate: 2020/5/22 10:18
 * @Description:
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PageRequestObject extends Instruction {
    /**
     * url
     */
    String url;

    /**
     * 连接 超时时间
     */
    int connectTimeout = 5;
    /**
     * 最大超时时间 假如一个文件 超过 MaxTimeout 还没下载完，就会返回
     */
    int maxTimeout = 10;

    /**
     * 是否使用重定向
     */
    int useRedirect = 1;

    /**
     * 表头 可以有多个
     */
    List<String> httpHeaders;

    Boolean hijacking = false;

    /**
     * 页面源代码中未找到如下预期正确文本内容时触发“预期的页面内容未找到”错误
     */

    String expectContaining;
    /**
     * 预期MD5验证
     */
    String md5  ;

    /**
     * 上一次执行时间
     */
    String lastExecuteTime;


    /**
     * 监测频率,单位小时
     */
    String monitorFrequency;
}
