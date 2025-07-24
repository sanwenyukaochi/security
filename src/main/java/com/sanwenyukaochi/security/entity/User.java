package com.sanwenyukaochi.security.entity;

import com.sanwenyukaochi.security.entity.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_user",
        indexes = {@Index(name = "uk_user_username", columnList = "created_by, user_name", unique = true)}
)
@EntityListeners(AuditingEntityListener.class)
@Comment("用户表")
public class User extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant", nullable = false,
            foreignKey = @ForeignKey(name = "fk_user_tenant"))
    @Comment("租户信息")
    private Tenant tenant;

    @Column(name = "user_name", length = 50, nullable = false)
    @Comment("用户名")
    private String userName;

    @Column(name = "nick_name", length = 50, nullable = false)
    @Comment("用户昵称")
    private String nickName;

    @Column(name = "password_hash", nullable = false)
    @Comment("用户密码")
    @NotBlank
    @Size(max = 120)
    private String password;

    @Column(name = "email", length = 100, nullable = false)
    @Comment("邮箱")
    @Email
    private String email;

    @Column(name = "phone", length = 20, nullable = false)
    @Comment("手机号")
    private String phone;

    @Column(name = "status", nullable = false)
    @Comment("状态（true=启用，false=禁用）")
    private Boolean status = true;

    @Column(name = "account_non_expired", nullable = false)
    @Comment("账户是否未过期（true=有效，false=过期）")
    private Boolean accountNonExpired = true;

    @Column(name = "account_non_locked", nullable = false)
    @Comment("账户是否未锁定（true=正常，false=锁定）")
    private Boolean accountNonLocked = true;

    @Column(name = "credentials_non_expired", nullable = false)
    @Comment("密码是否未过期（true=有效，false=已过期）")
    private Boolean credentialsNonExpired = true;
}
