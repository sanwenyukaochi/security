package com.sanwenyukaochi.security.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.Filter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_videos",
        indexes = {@Index(name = "uk_video_name", columnList = "created_by, name, tenant_id", unique = true)}
)
@EntityListeners(AuditingEntityListener.class)
@Comment("视频表")
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
public class Video extends BaseEntity {

    @Column(name = "name", length = 100, nullable = false)
    @Comment("视频名称")
    private String name;

}
