package com.sanwenyukaochi.security.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum DataScopeEnum {
    ALL(1, "全部数据"),
    TENANT(2, "本租户数据"),
    SELF(3, "本人数据");
    private final int code;
    private final String description;

    private static final Map<Integer, DataScopeEnum> CACHE = Arrays.stream(values()).collect(Collectors.toMap(DataScopeEnum::getCode, Function.identity()));

    public static DataScopeEnum from(int code) {
        return CACHE.getOrDefault(code, ALL);
    }
}