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
public class NodeGroup {
    /**
     * 名称
     */
    String nodeGroupName;
    /**
     * 描述
     */
    String description;
    /**
     * IDC 机房
     */
    String IDCName;
    /**
     * 运营商
     */
    String operator;
    /**
     * 地区
     */
    String zone;
    /**
     * 城市
     */
    List<String> city;

    /**
     * 节点数
     */
    int nodeGroupCount;
}
