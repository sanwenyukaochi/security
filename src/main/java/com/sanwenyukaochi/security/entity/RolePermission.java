package com.sanwenyukaochi.security.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.Filter;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Comment("角色权限关联表")
@Table(name = "sys_role_permissions",
        indexes = {@Index(name = "uk_role_permission", columnList = "role_id, permission_id", unique = true)}
)
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
public class RolePermission extends BaseIdEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_role_permission_role_id"))
    @Comment("角色ID")
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "permission_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_role_permission_permission_id"))
    @Comment("权限ID")
    private Permission permission;
}
