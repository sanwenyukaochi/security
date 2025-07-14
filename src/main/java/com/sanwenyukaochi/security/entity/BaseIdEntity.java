package com.sanwenyukaochi.security.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;

import java.io.Serializable;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass // JPA注解，表示父类字段会映射到子类实体的表中
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "tenantId", type = String.class))
public abstract class BaseIdEntity implements Serializable {

    @Id
    @Column(name = "id", nullable = false)
    @Comment("主键ID")
    private Long id;

    // @TenantId
    @Column(name = "tenant_id", nullable = false)
    @Comment("租户ID")
    private String tenantId;

}
