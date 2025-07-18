package com.sanwenyukaochi.security.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_user_roles",
        indexes = {@Index(name = "uk_user_role", columnList = "user_id, role_id", unique = true)}
)
@Comment("用户角色关联表")
public class UserRole extends BaseIdEntity {

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, 
            foreignKey = @ForeignKey(name = "fk_user_role_user_id"))
    @Comment("用户ID")
    private User user;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_user_role_role_id"))
    @Comment("角色ID")
    private Role role;
}
