package com.sanwenyukaochi.security.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "permission_id", nullable = false)
    private Permission permission;
}
