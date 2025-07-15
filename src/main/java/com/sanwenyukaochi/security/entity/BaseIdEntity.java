package com.sanwenyukaochi.security.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class BaseIdEntity implements Serializable {

    @Id
    @Column(name = "id", nullable = false)
    @Comment("主键ID")
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    @Comment("租户ID")
    private Long tenantId;
}
