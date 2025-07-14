package com.sanwenyukaochi.security.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class BaseEntity extends BaseIdEntity{
    
    @CreatedBy
    @Column(name = "created_by", nullable = false, updatable = false)
    @Comment("创建者")
    private Long createdBy;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    @Comment("创建时间")
    private Long createdAt;

    @LastModifiedBy
    @Column(name = "updated_by", nullable = false)
    @Comment("更新者")
    private Long updatedBy;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    @Comment("更新时间")
    private Long updatedAt;
}
