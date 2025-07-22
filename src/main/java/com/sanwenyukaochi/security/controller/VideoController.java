package com.sanwenyukaochi.security.controller;

import com.sanwenyukaochi.security.ao.DeleteVideoAO;
import com.sanwenyukaochi.security.ao.QueryVideoAO;
import com.sanwenyukaochi.security.ao.UpdateVideoAO;
import com.sanwenyukaochi.security.ao.UploadVideoAO;
import com.sanwenyukaochi.security.bo.VideoBO;
import com.sanwenyukaochi.security.dto.VideoDTO;
import com.sanwenyukaochi.security.service.VideoService;
import com.sanwenyukaochi.security.vo.QueryVideoVO;
import com.sanwenyukaochi.security.vo.Result;
import com.sanwenyukaochi.security.vo.UploadVideoVO;
import com.sanwenyukaochi.security.vo.page.PageVO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
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
        VideoBO newVideoBO = new VideoBO(
                FilenameUtils.getBaseName(uploadVideoAO.getVideoName()), 
                FilenameUtils.getExtension(uploadVideoAO.getVideoName())
        );
        VideoDTO newVideoDTO = videoService.uploadVideo(newVideoBO, authentication);
        return Result.success(new UploadVideoVO(
                newVideoDTO.getVideoName(), 
                newVideoDTO.getFileSize(), 
                newVideoDTO.getDuration(), 
                newVideoDTO.getVideoPath(), 
                newVideoDTO.getCoverImage()
        ));
    }

    @GetMapping("/queryVideo")
    public Result<PageVO<QueryVideoVO>> getVideo(@RequestParam(defaultValue = "0") int currentPage, @RequestParam(defaultValue = "6") int size, 
                                                 @RequestBody QueryVideoAO queryVideoAO) {
        // TODO 条件过滤这边后续要更新一下
        Pageable pageable = PageRequest.of(currentPage, size, 
                Sort.by("asc".equalsIgnoreCase(queryVideoAO.getOrder()) ? Sort.Direction.ASC : Sort.Direction.DESC, queryVideoAO.getSortType()));
        Page<VideoDTO> videoPage = videoService.findAllVideo(pageable);
        return Result.success(PageVO.from(videoPage.map(video -> new QueryVideoVO(
                video.getVideoName(),
                video.getFileSize(),
                video.getDuration(),
                video.getVideoPath(),
                video.getCoverImage()
        ))));
    }

    @PutMapping("/rename")
    @PreAuthorize("hasAuthority('video:video:update')")
    @Operation(summary = "重命名视频", description = "传入视频id, 视频名称")
    public Result<UploadVideoVO> updateVideo(@RequestBody UpdateVideoAO updateVideoAO) {
        VideoBO newVideoBO = new VideoBO(
                updateVideoAO.getId(), 
                FilenameUtils.getBaseName(updateVideoAO.getVideoName()), 
                FilenameUtils.getExtension(updateVideoAO.getVideoName())
        );
        VideoDTO newVideoDTO = videoService.updateVideo(newVideoBO);
        return Result.success(new UploadVideoVO(
                newVideoDTO.getVideoName(), 
                newVideoDTO.getFileSize(), 
                newVideoDTO.getDuration(), 
                newVideoDTO.getVideoPath(), 
                newVideoDTO.getCoverImage()
        ));
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('video:video:delete')")
    @Operation(summary = "删除视频", description = "传入视频id")
    public Result<String> deleteVideo(@RequestBody DeleteVideoAO deleteVideoAO, Authentication authentication) {
        VideoBO newVideoBO = new VideoBO(
                deleteVideoAO.getId()
        );
        videoService.deleteVideo(newVideoBO, authentication);
        return Result.success("删除成功");
    }
}
