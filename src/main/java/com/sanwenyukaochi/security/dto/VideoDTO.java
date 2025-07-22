package com.sanwenyukaochi.security.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoDTO {
    private String videoName;
    private Long fileSize;
    private Double duration;
    private String videoPath;
    private String coverImage;
}
