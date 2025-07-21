package com.sanwenyukaochi.security.bo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VideoBO {
    private Long id;
    private String fileName;
    private String fileExt;

    public VideoBO(Long id) {
        this.id = id;
    }
    
    public VideoBO(String fileName, String fileExt) {
        this.fileName = fileName;
        this.fileExt = fileExt;
    }
}
