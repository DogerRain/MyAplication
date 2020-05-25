package com.meizu.lastmile.requestObj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * @Author: huangyongwen
 * @CreateDate: 2020/5/21 11:00
 * @Description:
 */
@Data
/**
 * 附带ping操作
 */
public class Ping {

    /**
     * 主机
     */
    String host;


    String ip;

    int count;


}
