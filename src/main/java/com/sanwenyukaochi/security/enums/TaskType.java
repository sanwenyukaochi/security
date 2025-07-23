package com.sanwenyukaochi.security.enums;

import lombok.Getter;

@Getter
public enum TaskType {
    SLICE("视频切片"),
    OUTLINE("大纲生成"),
    MERGE("视频合成"),
    DOWNLOAD("视频下载");
    private final String type;
    TaskType(String type) {
        this.type = type;
    }
}
