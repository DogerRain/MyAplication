package com.meizu.lastmile.responseObj;

import lombok.Data;

/**
 * @Author: huangyongwen
 * @CreateDate: 2020/5/18 18:15
 * @CreateDate:
 */
@Data
public class PageResponseObject {
    /**
     * 总时间
     */
    String timeTotal;
    /**
     * DNS解析时间
     */
    String timeNamelookup;
    /**
     * 建立TCP连接时间
     */
    String timeConnect;
    /**
     * 从开始到准备传输的时间
     */
    String timePretransfer;
    /**
     * 首包时间（开始传输时间。在发出请求之后，Web 服务器返回数据的第一个字节所用的时间）
     */
    String timeStarttransfer;

    /**
     * 总下载字节数
     */
    String sizeDownload;

    /**
     * header下载大小
     */
    String sizeHeader;

    /**
     * 下载速度
     */
    String speedDownload;
    /**
     * 重定向时间
     */
    String timeRedirect;
    /**
     * 重定向次数
     */
    String numRedirects;


    /**
     * 返回码
     */
    String responseCode;

    /**
     * 返回类型
     */
    String contentType;

    /**
     * 客户端时间
     */
    String clientTime;
    /**
     * 可用性
     */
    String usability;


}
