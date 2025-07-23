package com.sanwenyukaochi.security.entity;

import com.sanwenyukaochi.security.entity.base.BaseEntity;
import com.sanwenyukaochi.security.entity.relation.ClipTag;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_clip")
@EntityListeners(AuditingEntityListener.class)
@Comment("切片表")
public class Clip extends BaseEntity {

    @Column(name = "start", nullable = false)
    @Comment("切片开始时间")
    private String startTime;

    @Column(name = "end", nullable = false)
    @Comment("切片结束时间")
    private String endTime;

    @Column(name = "order_in_group", nullable = false)
    @Comment("组内排序值")
    private Integer orderInGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clip_group_id")
    @Comment("所属分组")
    private ClipGroup clipGroup;

    @Column(name = "subtitles", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    @Comment("句子信息")
    private List<Subtitle> subtitles;

    @OneToMany(mappedBy = "clip", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClipTag> clipTags;


    @Data
    private static class Subtitle {
        private String start;
        private String end;
        private String text;
    }
}
