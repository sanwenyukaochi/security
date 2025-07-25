package com.sanwenyukaochi.security.dto;

import com.sanwenyukaochi.security.enums.TaskType;
import com.sanwenyukaochi.security.enums.VideoType;

public record AgentVideoSliceDTO(
        String videoName,
        String videoPath,
        TaskType taskType,
        VideoType videoType,
        String callbackUrl,
        Long taskId,
        Integer adaptiveThreshold,
        boolean addSubtitle
) {}