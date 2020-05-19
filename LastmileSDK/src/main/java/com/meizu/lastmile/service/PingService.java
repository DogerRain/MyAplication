package com.meizu.lastmile.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.meizu.lastmile.Utils.DatabaseHelper;
import com.meizu.lastmile.requestObj.Ping.PingRequestObject;
import com.meizu.lastmile.responseObj.PingResponseObject;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

/**
 * @Author： Administrator
 * @CreateDate： 2020/5/18 21:12
 * @Descriotion：接收任务，并存储在本地
 */

@Slf4j
public class PingService {
    public PingResponseObject receiveInstructionAndStorage(PingRequestObject pingRequestObject, Context context) {
        if (pingRequestObject == null) {
            return null;
        }
        if (StringUtils.isBlank(pingRequestObject.getIp())
                && StringUtils.isBlank(pingRequestObject.getHostName())
                ) {
            return null;
        }

        //任务id

        String tableName = "t_ping_command";
        String tableStructure = "create table t_ping_command(" +
                "id int," +
                "name varchar(50)," +
                "command text," +
                "start_time varchar(30)," +
                "end_time varchar(30),";

        String target = pingRequestObject.getIp() != null ? pingRequestObject.getIp() : pingRequestObject.getHostName();

        StringBuffer command = new StringBuffer();
        command.append("ping " + target);
        //ping次数 默认是4次
        if (StringUtils.isNotBlank(pingRequestObject.getCount())) {
            command.append(" -c " + pingRequestObject.getCount());
        }
        //windows是 -l ；Linux是 -s
        if (StringUtils.isNotBlank(pingRequestObject.getPackageSize())) {
            command.append(" -s " + pingRequestObject.getPackageSize());
        }
        //单位是ms
        if (StringUtils.isNotBlank(pingRequestObject.getTimeout())) {
            command.append(" -w " + pingRequestObject.getTimeout());
        }

        //生成任务，存在本地数据库
        DatabaseHelper dbHelper1 = new DatabaseHelper(context, "ping_task_db");
        //取得一个只读的数据库对象
        SQLiteDatabase db1 = dbHelper1.getWritableDatabase();

        //获取表是否存在
        if (!dbHelper1.IsTableExist(db1, tableName)){
            dbHelper1.createTable(db1,tableStructure);
        }


        return null;
    }

    public static void main(String[] args) throws IOException, InterruptedException {

    }
}
