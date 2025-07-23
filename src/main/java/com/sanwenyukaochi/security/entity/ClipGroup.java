package com.sanwenyukaochi.security.entity;

import com.sanwenyukaochi.security.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_clip_group")
@EntityListeners(AuditingEntityListener.class)
@Comment("切片组表")
public class ClipGroup extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "video_id", nullable = false)
    @Comment("所属原视频")
    private Video video;
    
    @Column(name = "summary", nullable = false)
    @Comment("总结")
    private String summary;
    
    @Column(name = "start", nullable = false)
    @Comment("开始时间")
    private String start;
    
    @Column(name = "end", nullable = false)
    @Comment("结束时间")
    private String end;

    @Column(name = "group_order", nullable = false)
    @Comment("组在视频中的排序")
    private Integer groupOrder;
    
    @OneToMany(mappedBy = "clipGroup", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Clip> clips = new ArrayList<>();
}
