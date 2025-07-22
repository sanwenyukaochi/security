package com.sanwenyukaochi.security.bo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VideoBO {
    private Long id;
    private String fileName;
    private String fileExt;
    private Boolean hasClips;

    public VideoBO(Long id) {
        this.id = id;
    }
    
    public VideoBO(String fileName, String fileExt) {
        this.fileName = fileName;
        this.fileExt = fileExt;
    }

    public VideoBO(Boolean hasClips) {
        this.hasClips = hasClips;
    }

    public VideoBO(Long id, String fileName, String fileExt) {
        this.id = id;
        this.fileName = fileName;
        this.fileExt = fileExt;
    }
}
