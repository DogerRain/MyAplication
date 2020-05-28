package com.meizu.lastmile.responseObj;

import java.math.BigDecimal;

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
    BigDecimal timeTotal;
    /**
     * DNS解析时间
     */
    BigDecimal timeNamelookup;
    /**
     * 建立TCP连接时间
     */
    BigDecimal timeTCP;

    /**
     * SSL握手时间
     */
    BigDecimal timeSSL;
    /**
     * 从开始到准备传输的时间
     */
    BigDecimal timePretransfer;
    /**
     * 首包时间（开始传输时间。在发出请求之后，Web 服务器返回数据的第一个字节所用的时间）
     */
    BigDecimal timeStarttransfer;

    /**
     * 总下载字节数
     */
    BigDecimal sizeDownload;

    /**
     * header下载大小
     */
    BigDecimal sizeHeader;

    /**
     * 下载速度
     */
    BigDecimal speedDownload;
    /**
     * 重定向时间
     */
    BigDecimal timeRedirect;
    /**
     * 重定向次数
     */
    String numRedirects;
    /**
     * 最近传输中建立的新连接数
     */
//    String numConnects;

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
    BigDecimal clientTime;

    /**
     * 内容下载时间
     */
    BigDecimal downloadTime;
    /**
     * 可用性
     */
    String usability;


    Boolean result;
    StringBuffer resultBuffer;


}
