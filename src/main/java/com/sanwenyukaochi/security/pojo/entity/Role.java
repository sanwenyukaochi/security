package com.sanwenyukaochi.security.pojo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SoftDelete;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "roles")
@EntityListeners(AuditingEntityListener.class)
@SoftDelete
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    
    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    
    @Column(name = "role_name", nullable = false)
    private String roleName;
    
    @Column(name = "role_code", nullable = false)
    private String roleCode;
}
