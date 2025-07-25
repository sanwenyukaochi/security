package com.sanwenyukaochi.security.ao;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.sanwenyukaochi.security.enums.TaskType;
import com.sanwenyukaochi.security.enums.VideoType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoSliceAO {
        @JsonSerialize(using = ToStringSerializer.class)
        private Long id;
        private TaskType taskType;
        private VideoType videoType;
        private Integer adaptiveThreshold;
        private boolean addSubtitle;
}