package com.sanwenyukaochi.security.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_users", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_username", columnNames = "user_name")
})
@EntityListeners(AuditingEntityListener.class)
@Comment("用户表")
public class User extends BaseEntity{
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant", nullable = false)
    @Comment("租户信息")
    private Tenant tenant;

    @Column(name = "user_name", length = 50, nullable = false)
    @Comment("用户名")
    private String userName;

    @Column(name = "password_hash", nullable = false)
    @Comment("用户密码")
    @NotBlank
    @Size(max = 120)
    private String password;

    @Column(name = "email", length = 100, nullable = false)
    @Comment("邮箱")
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

    // 用户角色关联
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<UserRole> userRoles = new ArrayList<>();

    public User(String userName, String email, String password) {
        this.userName = userName;
        this.email = email;
        this.password = password;
    }
}
