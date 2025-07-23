package com.sanwenyukaochi.security.entity;

import com.sanwenyukaochi.security.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_tenant", 
        uniqueConstraints = {@UniqueConstraint(name = "uk_tenant_name", columnNames = "name")}
)
@EntityListeners(AuditingEntityListener.class)
@Comment("租户表")
public class Tenant extends BaseEntity {
    
    @Column(name = "name", length = 100, nullable = false)
    @Comment("租户名称")
    private String name;

    @Column(name = "code", length = 50, nullable = false, unique = true)
    @Comment("租户编码，唯一")
    private String code;

    @Column(name = "status", nullable = false)
    @Comment("状态（true=启用，false=禁用）")
    private Boolean status = true;
}
