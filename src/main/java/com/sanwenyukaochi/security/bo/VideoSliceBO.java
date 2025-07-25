package com.sanwenyukaochi.security.bo;

import com.sanwenyukaochi.security.enums.TaskType;
import com.sanwenyukaochi.security.enums.VideoType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VideoSliceBO {
    private Long id;
    private TaskType taskType;
    private VideoType videoType;
    private Integer adaptiveThreshold;
    private Boolean addSubtitle;
}
