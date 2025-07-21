package com.sanwenyukaochi.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoDTO {
    private String videoName;
    private String videoPath;
    private String coverImage;
}
