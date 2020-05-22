package com.meizu.lastmile.requestObj;

import java.util.List;

import lombok.Data;

/**
 * @Author: huangyongwen
 * @CreateDate: 2020/5/18 18:03
 * @CreateDate:
 */
@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
public class Group {
    /**
     * 名称
     */
    String idc;


    List<String> isp;

    /**
     * 城市
     */
    List<String> cities;


    /**
     * 节点数
     */
    int groupCount;
}
