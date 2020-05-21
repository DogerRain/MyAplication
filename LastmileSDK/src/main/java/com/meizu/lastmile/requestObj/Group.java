package com.meizu.lastmile.requestObj;

import java.util.List;

import lombok.Builder;
import lombok.Data;

/**
 * @Author: huangyongwen
 * @CreateDate: 2020/5/18 18:03
 * @CreateDate:
 */
@Data
@Builder
public class Group {
    /**
     * 名称
     */
    String idc;


    List<String> isp;

    /**
     * 城市
     */
    List<String> citis;


    /**
     * 节点数
     */
    int groupCount;
}
