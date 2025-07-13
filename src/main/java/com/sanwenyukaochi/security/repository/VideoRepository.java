package com.sanwenyukaochi.security.repository;

import com.sanwenyukaochi.security.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

    /**
     * 根据名称查找视频
     */
    Optional<Video> findByName(String name);

    /**
     * 根据创建者查找视频列表
     */
    List<Video> findByCreatedBy(Long createdBy);

    /**
     * 根据名称和创建者查找视频
     */
    Optional<Video> findByNameAndCreatedBy(String name, Long createdBy);

    /**
     * 检查指定名称的视频是否存在
     */
    Boolean existsByName(String name);
} 