package com.sanwenyukaochi.security.service;

import com.sanwenyukaochi.security.annotation.DataScope;
import com.sanwenyukaochi.security.entity.Video;
import com.sanwenyukaochi.security.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoService {
    
    private final VideoRepository videoRepository;
    
    @DataScope
    public Page<Video> findAllVideo(Pageable pageable) {
        return videoRepository.findAll(pageable);
    }
}
