package com.meizu.lastmile.constants;

/**
 * @Author: huangyongwen
 * @CreateDate: 2020/5/22 15:10
 * @Description:
 */

public class ConstantUtils {
    public static final String PING = "ping";
    public static final String PAGE = "page";
    public static final String DOWNLOAD = "download";

    public static String T_TASK = "t_task";
    public static String T_PING = "t_ping";
    public static String T_PAGE = "t_page";
    public static String T_PAGE_DOWNLOAD = "t_page_download";
    public static String T_DOWLOAD = "t_download";


    public static String TEST_APP_NOMAL_KEY ="3JAH1B2FFS1R4X8AAX1ZFN02";

//    public static String REMOTE_URL = "http://lastmile.meizu.com:8085/task/findall";
    public static String REMOTE_URL = "http://172.16.44.103:8085/task/findall";


    public static boolean SWITCH = true;





    public static String[] PAGE_DOWNLOAD_KEY = {"response_code", "content_type", "time_namelookup", "time_redirect", "num_redirects",
            "time_connect", "time_appconnect", "time_pretransfer", "time_starttransfer", "time_total", "size_header", "size_download", "speed_download"};

}
