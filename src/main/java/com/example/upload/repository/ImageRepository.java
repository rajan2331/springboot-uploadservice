package com.example.upload.repository;

import com.example.upload.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    
    Optional<Image> findByImageId(String imageId);
    
    List<Image> findByUserId(String userId);
    
    List<Image> findByStatus(Image.ProcessingStatus status);
}