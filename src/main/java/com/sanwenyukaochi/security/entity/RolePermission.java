package com.sanwenyukaochi.security.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "sys_role_permissions",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_role_permission", columnNames = {"role_id", "permission_id"})
        }
)
public class RolePermission extends BaseIdEntity {

    @Column(name = "permission_id", nullable = false)
    private Long permissionId;

    @Column(name = "role_id", nullable = false)
    private Long roleId;
}
