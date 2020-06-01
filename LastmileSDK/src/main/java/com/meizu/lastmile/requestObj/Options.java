package com.meizu.lastmile.requestObj;

import lombok.Data;

/**
 * @Author: huangyongwen
 * @CreateDate: 2020/5/21 11:00
 * @Description: 用户ip、运营商、位置
 */
@Data
public class Options {
    /**
     * 用户的ip ，不能为空
     */
    String ip;
    /**
     * 运营商 （电信、移动、联通、其他）、可为空
     */
    String operation;
    /**
     * 用户位置、可为空
     */
    String location;

}
