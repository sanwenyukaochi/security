package com.sanwenyukaochi.security.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UploadVideoVO {
    private String videoName;
    private String videoPath;
    private String coverImage;
}
