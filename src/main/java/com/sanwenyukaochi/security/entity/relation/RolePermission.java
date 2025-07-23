package com.sanwenyukaochi.security.entity.relation;

import com.sanwenyukaochi.security.entity.base.BaseIdEntity;
import com.sanwenyukaochi.security.entity.Permission;
import com.sanwenyukaochi.security.entity.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_role_permission_rel",
        indexes = {@Index(name = "uk_role_permission", columnList = "role_id, permission_id", unique = true)}
)
@Comment("角色权限关联表")
public class RolePermission extends BaseIdEntity {

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_role_permission_role_id"))
    @Comment("角色Id")
    private Role role;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "permission_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_role_permission_permission_id"))
    @Comment("权限Id")
    private Permission permission;
}
