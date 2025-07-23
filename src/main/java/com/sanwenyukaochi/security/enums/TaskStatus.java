package com.sanwenyukaochi.security.enums;

import lombok.Getter;

@Getter
public enum TaskStatus {
    QUEUED("排队中"),
    PENDING("处理中"),
    DOWNLOADING("下载中"),
    FINISHED("已完成"),
    FAILED("失败");
    private final String status;
    TaskStatus(String status) {
        this.status = status;
    }
}
