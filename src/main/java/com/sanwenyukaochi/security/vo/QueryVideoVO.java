package com.sanwenyukaochi.security.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class QueryVideoVO {
    private String videoName;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long fileSize;
    @JsonSerialize(using = ToStringSerializer.class)
    private Double duration;
    private String videoPath;
    private String coverImage;
}
