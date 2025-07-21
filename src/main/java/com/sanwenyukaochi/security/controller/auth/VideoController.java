package com.sanwenyukaochi.security.controller.auth;

import com.sanwenyukaochi.security.ao.UploadVideoAO;
import com.sanwenyukaochi.security.dto.VideoDTO;
import com.sanwenyukaochi.security.entity.Video;
import com.sanwenyukaochi.security.service.VideoService;
import com.sanwenyukaochi.security.vo.Result;
import com.sanwenyukaochi.security.vo.UploadVideoVO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.apache.commons.io.FilenameUtils;


@Slf4j
@RestController
@RequestMapping("/api/video")
@RequiredArgsConstructor
public class VideoController {
    private final VideoService videoService;

    @PostMapping("/upload")
    @PreAuthorize("hasAuthority('video:video:upload')")
    @Operation(summary = "上传视频", description = "传入视频名称videoName")
    public Result<UploadVideoVO> uploadVideo(@RequestBody UploadVideoAO uploadVideoAO, Authentication authentication) {
        VideoDTO videoDTO = new VideoDTO(FilenameUtils.getBaseName(uploadVideoAO.getVideoName()), FilenameUtils.getExtension(uploadVideoAO.getVideoName()));
        Video video = videoService.uploadVideo(videoDTO, authentication);
        return Result.success(new UploadVideoVO(String.format("%s.%s",video.getFileName(),video.getFileExt()), video.getVideoPath(), video.getCoverImage()));
    }


    
    
    
}
