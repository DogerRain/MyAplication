package com.meizu.lastmile.requestObj;

/**
 * @Author: huangyongwen
 * @CreateDate: 2020/5/22 14:21
 * @Description:
 */

public enum TaskType {
    PING(1, "ping"), PAGE(2, "page"), DOWNLOAD(3, "download");

    private int id;
    private String taskType;

    TaskType(int id, String taskType) {
        this.id = id;
        this.taskType = taskType;
    }

    public String getTaskType() {
        return taskType;
    }
}
