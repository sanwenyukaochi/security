package com.sanwenyukaochi.security.service;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.http.HttpStatus;
import com.sanwenyukaochi.security.annotation.DataScope;
import com.sanwenyukaochi.security.constant.FileConstants;
import com.sanwenyukaochi.security.bo.VideoBO;
import com.sanwenyukaochi.security.dto.VideoDTO;
import com.sanwenyukaochi.security.entity.Video;
import com.sanwenyukaochi.security.exception.APIException;
import com.sanwenyukaochi.security.repository.VideoRepository;
import com.sanwenyukaochi.security.security.service.UserDetailsImpl;
import com.sanwenyukaochi.security.storage.FileStorage;
import com.sanwenyukaochi.security.utils.FfmpegUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import cn.hutool.core.io.FileUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoService {
    private final VideoRepository videoRepository;
    private final FileStorage fileStorage;
    private final Snowflake snowflake;
    @Value("${storage.sfs.local-dir}")
    private String localDir;

    @DataScope
    public Page<VideoDTO> findAllVideo(VideoBO newVideoBO, Pageable pageable) {
        Page<Video> all = newVideoBO.getHasClips() == null ? videoRepository.findAll(pageable) : videoRepository.findAllByHasClips(newVideoBO.getHasClips(), pageable);
        return all.map(newVideo -> new VideoDTO(
                newVideo.getFullFileNameWithName(),
                newVideo.getFileSize(),
                newVideo.getDuration(),
                newVideo.getVideoPath(),
                newVideo.getCoverImage()
        ));
    }

    @Transactional
    @SneakyThrows
    public VideoDTO uploadVideo(VideoBO videoBO, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Video newVideo = new Video();
        newVideo.setId(snowflake.nextId());
        newVideo.setFileName(videoBO.getFileName());
        newVideo.setFileExt(videoBO.getFileExt());
        // 构建新视频对象路径
        String renamedObjectPath = String.format("%s/%s/%s/%s.%s", userDetails.getTenant().getId(), userDetails.getId(), newVideo.getId(), newVideo.getId(), videoBO.getFileExt());
        newVideo.setVideoPath(String.format("%s/%s", fileStorage.getBucketPath(), renamedObjectPath));
        // 构建封面图路径
        String coverObjectPath = String.format("%s/%s/%s/%s/%s.%s", userDetails.getTenant().getId(), userDetails.getId(), newVideo.getId(), FileConstants.COVER_DIR_NAME, newVideo.getId(), FileConstants.EXT_JPG);
        newVideo.setCoverImage(String.format("%s/%s", fileStorage.getBucketPath(), coverObjectPath));
        // 修改 OBS 上视频对象名
        String originalObjectPath = String.format("%s/%s/%s.%s", userDetails.getTenant().getId(), userDetails.getId(), videoBO.getFileName(), videoBO.getFileExt());
        fileStorage.renameObject(originalObjectPath, renamedObjectPath);
        // 下载 OBS 视频到本地
        Path videoObjectPath = Paths.get(renamedObjectPath);
        Path localVideoPath = Paths.get(localDir).resolve(videoObjectPath);
        FileUtil.mkdir(localVideoPath.getParent().toFile());
        if (!FileUtil.exist(localVideoPath.toFile())) {fileStorage.downloadFileByCheckpoint(videoObjectPath.toString(), localVideoPath.toString());}
        // 生成封面图
        Path localCoverPath = Paths.get(localDir, coverObjectPath);
        if (localCoverPath.getParent() != null && Files.notExists(localCoverPath.getParent())) {Files.createDirectories(localCoverPath.getParent());}
        FfmpegUtils.getFirstImageFromVideo(localVideoPath.toString(), "00:00:00.000", localCoverPath.toString());
        // 上传封面图至 OBS
        fileStorage.uploadFileByFileStream(coverObjectPath, localCoverPath.toString());
        newVideo.setFileSize(new File(localVideoPath.toString()).length());
        newVideo.setDuration(FfmpegUtils.getVideoDuration(localVideoPath.toString()));
        newVideo.setTenantId(userDetails.getTenant().getId());
        videoRepository.save(newVideo);
        return new VideoDTO(
                newVideo.getFullFileNameWithName(),
                newVideo.getFileSize(),
                newVideo.getDuration(),
                newVideo.getVideoPath(),
                newVideo.getCoverImage()
        );
    }

    @Transactional
    public void deleteVideo(VideoBO videoBO, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Video dbVideo = videoRepository.findById(videoBO.getId()).orElseThrow(() -> new APIException(HttpStatus.HTTP_NOT_FOUND, "视频不存在"));
        String renamedObjectPath = String.format("%s/%s/%s", userDetails.getTenant().getId(), userDetails.getId(), videoBO.getId());
        fileStorage.deleteObject(String.format(renamedObjectPath));
        Path localVideoFolderPath = Paths.get(localDir, renamedObjectPath);
        if (FileUtil.exist(localVideoFolderPath.toFile())) {FileUtil.del(localVideoFolderPath);}
        videoRepository.delete(dbVideo);
    }

    public VideoDTO updateVideo(VideoBO videoBO) {
        Video dbVideo = videoRepository.findById(videoBO.getId()).orElseThrow(() -> new APIException(HttpStatus.HTTP_NOT_FOUND, "视频不存在"));
        dbVideo.setFileName(videoBO.getFileName());
        dbVideo.setFileExt(videoBO.getFileExt());
        videoRepository.save(dbVideo);
        return new VideoDTO(
                dbVideo.getFullFileNameWithName(),
                dbVideo.getFileSize(),
                dbVideo.getDuration(),
                dbVideo.getVideoPath(),
                dbVideo.getCoverImage()
        );
    }
}
