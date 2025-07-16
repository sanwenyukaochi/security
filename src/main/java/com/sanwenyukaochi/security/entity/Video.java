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

}
