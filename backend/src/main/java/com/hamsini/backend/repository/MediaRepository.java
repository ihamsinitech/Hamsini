package com.hamsini.backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.hamsini.backend.model.MediaItem;

public interface MediaRepository extends JpaRepository<MediaItem, Long> {
    List<MediaItem> findByTypeOrderByUploadDateDesc(String type);
    List<MediaItem> findAllByOrderByUploadDateDesc();
}