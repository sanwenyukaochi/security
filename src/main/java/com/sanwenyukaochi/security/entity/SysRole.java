package com.sanwenyukaochi.security.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "sys_roles")
@EntityListeners(AuditingEntityListener.class)
public class SysRole extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant", nullable = false)
    @Comment("租户信息")
    private SysTenant tenant;

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
}
