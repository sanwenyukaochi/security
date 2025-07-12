package com.sanwenyukaochi.security.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_tenants", uniqueConstraints = {
        @UniqueConstraint(name = "uk_tenant_name", columnNames = "name")
})
@EntityListeners(AuditingEntityListener.class)
@Comment("租户表")
public class Tenant {

    @Id
    @Column(name = "id", nullable = false)
    @Comment("主键ID")
    private Long id;
    
    @Column(name = "name", length = 100, nullable = false)
    @Comment("租户名称")
    private String name;

    @Column(name = "code", length = 50, nullable = false, unique = true)
    @Comment("租户编码，唯一")
    private String code;

    @Column(name = "status", nullable = false)
    @Comment("状态（true=启用，false=禁用）")
    private Boolean status = true;

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
