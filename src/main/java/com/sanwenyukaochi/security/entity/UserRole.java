package com.sanwenyukaochi.security.entity;

import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_user_roles",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_role", columnNames = {"user_id", "role_id"})
        }
)
public class UserRole extends BaseIdEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
}
