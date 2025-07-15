package com.sanwenyukaochi.security.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnore;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_roles",
        indexes = {@Index(name = "uk_role_code", columnList = "created_by, code, tenant_id", unique = true)}
)
@EntityListeners(AuditingEntityListener.class)
@Comment("角色表")
public class Role extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant", nullable = false)
    @Comment("租户信息")
    private Tenant tenant;

    @Column(name = "name", length = 100, nullable = false)
    @Comment("角色名称")
    private String name;

    @Column(name = "code", length = 100, nullable = false)
    @Comment("权限标识（如 sys:admin）")
    private String code;

    @Column(name = "data_scope")
    @Comment("数据权限（0=租户,1=本人,3=自定义）")
    private Integer dataScope;

    @Column(name = "status", nullable = false)
    @Comment("状态（false禁用，true启用）")
    private Boolean status = false;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<UserRole> userRoles = new ArrayList<>();

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<RolePermission> rolePermissions = new HashSet<>();
}
