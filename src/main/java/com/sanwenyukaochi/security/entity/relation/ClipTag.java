package com.sanwenyukaochi.security.entity.relation;

import com.sanwenyukaochi.security.entity.base.BaseIdEntity;
import com.sanwenyukaochi.security.entity.Clip;
import com.sanwenyukaochi.security.entity.Tag;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_clip_tag_rel")
@EntityListeners(AuditingEntityListener.class)
@Comment("切片标签关联表")
public class ClipTag extends BaseIdEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clip_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_clip_tag_clip_id"))
    @Comment("切片Id")
    private Clip clip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_clip_tag_tag_id"))
    @Comment("标签Id")
    private Tag tag;
}
