package com.meizu.lastmile.responseObj;

import com.meizu.lastmile.Utils.CommonUtils;

import java.math.BigDecimal;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: huangyongwen
 * @CreateDate: 2020/5/18 18:15
 * @CreateDate:
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PageResponseObject extends CommonResponseObject{

    /**
     * 总时间 单位为 ms ，以下均是 ms
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
     * 总下载字节数 单位 KB ，以下均是KB
     */
    BigDecimal sizeDownload;

    /**
     * header下载大小
     */
    BigDecimal sizeHeader;

    /**
     * 下载速度 单位 KB/s
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

    public static void main(String[] args) {
        PageResponseObject pageResponseObject = new PageResponseObject();
        pageResponseObject.setUsability("1");
        pageResponseObject.setResult(true);

        System.out.println(CommonUtils.jsonObjectToMap(pageResponseObject));
    }

}
