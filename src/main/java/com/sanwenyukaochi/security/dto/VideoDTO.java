package com.sanwenyukaochi.security.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VideoDTO {
    private Long id;
    private String fileName;
    private String fileExt;

    public VideoDTO(Long id) {
        this.id = id;
    }
    
    public VideoDTO(String fileName, String fileExt) {
        this.fileName = fileName;
        this.fileExt = fileExt;
    }
}
