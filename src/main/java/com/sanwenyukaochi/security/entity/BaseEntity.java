package com.sanwenyukaochi.security.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.TenantId;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass // JPA注解，表示父类字段会映射到子类实体的表中
public abstract class BaseEntity implements Serializable {

    @Id
    @Column(name = "id", nullable = false)
    @Comment("主键ID")
    private Long id;
    
    @TenantId
    @Column(name = "tenant_id", nullable = false)
    @Comment("租户ID")
    private Long tenantId;
    
    @CreatedBy
    @Column(name = "created_by", updatable = false)
    @Comment("创建者")
    private Long createdBy;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    @Comment("创建时间")
    private Long createdAt;

    @LastModifiedBy
    @Column(name = "updated_by")
    @Comment("更新者")
    private Long updatedBy;

    @LastModifiedDate
    @Column(name = "updated_at")
    @Comment("更新时间")
    private Long updatedAt;
}
