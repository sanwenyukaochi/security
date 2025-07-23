package com.sanwenyukaochi.security.entity;

import com.sanwenyukaochi.security.entity.base.BaseEntity;
import com.sanwenyukaochi.security.entity.relation.ClipTag;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_tag")
@EntityListeners(AuditingEntityListener.class)
@Comment("标签表")
public class Tag extends BaseEntity {
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "type", nullable = false)
    private String type;

    @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClipTag> clipTags;
}
