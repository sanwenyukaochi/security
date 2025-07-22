package com.sanwenyukaochi.security.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_videos",
        indexes = {@Index(name = "uk_video_name", columnList = "created_by, file_name, file_ext", unique = true)}
)
@EntityListeners(AuditingEntityListener.class)
@Comment("视频表")
public class Video extends BaseEntity {

    @Column(name = "file_name", length = 150, nullable = false)
    @Comment("文件存储名称，带后缀")
    private String fileName;

    @Column(name = "file_ext", length = 20, nullable = false)
    @Comment("文件扩展名（后缀，不带点）")
    private String fileExt;

    @Column(name = "file_size", nullable = false)
    @Comment("视频文件大小，单位字节")
    private Long fileSize;

    @Column(name = "duration", nullable = false)
    @Comment("视频时长，单位秒")
    private Double duration;

    @Column(name = "video_path", length = 225, nullable = false)
    @Comment("视频存储路径")
    private String videoPath;

    @Column(name = "cover_image", length = 225)
    @Comment("视频封面存储路径")
    private String coverImage;

    @Column(name = "has_clips", nullable = false)
    @Comment("是否有视频切片，true表示有")
    private Boolean hasClips = false;
    
    @Column(name = "has_outline", nullable = false)
    @Comment("是否有视频目录，true表示有")
    private Boolean hasOutline = false;
    
    public String getFullFileNameWithName() {
        return String.format("%s.%s", fileName, fileExt);
    }

    public String getFullFileNameWithId() {
        return String.format("%s.%s", getId(), fileExt);
    }
}
