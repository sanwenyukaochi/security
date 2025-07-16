package com.sanwenyukaochi.security.service;

import com.sanwenyukaochi.security.annotation.DataScope;
import com.sanwenyukaochi.security.entity.Video;
import com.sanwenyukaochi.security.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoService {
    
    private final VideoRepository videoRepository;
    
    @DataScope
    public List<Video> findAllVideo() {
        return videoRepository.findAll();
    }
}
