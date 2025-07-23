package com.sanwenyukaochi.security.enums;

import lombok.Getter;

@Getter
public enum ClipType {
    SHORT_DRAMA("短剧"),
    LIVE_STREAM("直播");
    private final String type;
    ClipType(String type) {
        this.type = type;
    }
}
