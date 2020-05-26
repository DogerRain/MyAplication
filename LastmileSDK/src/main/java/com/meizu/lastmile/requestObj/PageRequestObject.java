package com.meizu.lastmile.requestObj;

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
     * 超时时间
     */
    int timeout = 5;

    int redirects;

    String httpHeaders;

    Boolean hijacking =false;

    /**
     * // 页面源代码中未找到如下预期正确文本内容时触发“预期的页面内容未找到”错误
     */

    String expectContaining;



}
