package com.meizu.lastmile.requestObj;

import lombok.Data;

/**
 * @Author: huangyongwen
 * @CreateDate: 2020/5/21 11:00
 * @Description: 用户ip、运营商、位置
 */
@Data
public class Options {

    String ip;
    String operation;
    String location;

}
