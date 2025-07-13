package com.sanwenyukaochi.security.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_permissions",
        uniqueConstraints = {@UniqueConstraint(name = "uk_permission_code", columnNames = "code")}
)
@EntityListeners(AuditingEntityListener.class)
@Comment("权限表")
public class Permission  extends BaseEntity {

    @Column(name = "parent_id", nullable = false)
    @Comment("父节点ID（0为根）")
    private Long parentId;

    @Column(name = "type", length = 100, nullable = false)
    @Comment("类型（1菜单 2按钮）")
    private String type;

    @Column(name = "name", length = 100, nullable = false)
    @Comment("权限名称")
    private String name;

    @Column(name = "code", length = 100, nullable = false)
    @Comment("权限标识（如 user:add）")
    private String code;

    @Column(name = "path", length = 255)
    @Comment("前端路由/按钮绑定路径")
    private String path;

    @Column(name = "sort")
    @Comment("排序号")
    private Integer sort;

    @Column(name = "visible")
    @Comment("是否可见（true可见，false隐藏）")
    private Boolean visible;

    // 权限角色关联
    @OneToMany(mappedBy = "permission", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<RolePermission> rolePermissions = new ArrayList<>();
}
