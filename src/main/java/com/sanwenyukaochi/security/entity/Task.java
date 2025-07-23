package com.sanwenyukaochi.security.entity;

import com.sanwenyukaochi.security.entity.base.BaseEntity;
import com.sanwenyukaochi.security.enums.TaskStatus;
import com.sanwenyukaochi.security.enums.TaskType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_task")
@EntityListeners(AuditingEntityListener.class)
@Comment("标签表")
public class Task extends BaseEntity {
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Comment("任务类型")
    private TaskType type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Comment("任务状态")
    private TaskStatus status;
}
