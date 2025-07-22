package com.sanwenyukaochi.security.repository;

import com.sanwenyukaochi.security.entity.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

    Optional<Video> findByFileNameAndFileExt(String fileName, String fileExt);

    List<Video> findByCreatedBy(Long createdBy);

    Optional<Video> findByFileNameAndFileExtAndCreatedBy(String fileName, String fileExt, Long createdBy);

    Boolean existsByFileNameAndFileExt(String fileName, String fileExt);
    
    Page<Video> findAllByHasClips(Boolean hasClips, Pageable pageable);
}