package com.example.upload.controller;

import com.example.upload.dto.ImageResponse;
import com.example.upload.dto.UploadRequest;
import com.example.upload.dto.UploadResponse;
import com.example.upload.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
@Slf4j
public class ImageController {

    private final ImageService imageService;

    /**
     * Step 1: Client requests presigned URL for uploading
     * POST /api/images/upload/initiate
     */
    @PostMapping("/upload/initiate")
    public ResponseEntity<UploadResponse> initiateUpload(@RequestBody UploadRequest request) {
        log.info("Received upload initiation request: userId={}, fileName={}", 
                request.getUserId(), request.getFileName());
        
        UploadResponse response = imageService.initiateUpload(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Step 2: After client uploads to S3 using presigned URL, they confirm completion
     * POST /api/images/upload/confirm/{imageId}
     * 
     * This triggers async processing via Kafka
     */
    @PostMapping("/upload/confirm/{imageId}")
    public ResponseEntity<Void> confirmUpload(@PathVariable String imageId) {
        log.info("Received upload confirmation: imageId={}", imageId);
        
        imageService.confirmUpload(imageId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    /**
     * Get image metadata by ID
     * GET /api/images/{imageId}
     */
    @GetMapping("/{imageId}")
    public ResponseEntity<ImageResponse> getImage(@PathVariable String imageId) {
        log.info("Fetching image: imageId={}", imageId);
        
        ImageResponse response = imageService.getImage(imageId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all images for a user
     * GET /api/images/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ImageResponse>> getUserImages(@PathVariable String userId) {
        log.info("Fetching images for user: userId={}", userId);
        
        List<ImageResponse> images = imageService.getUserImages(userId);
        return ResponseEntity.ok(images);
    }
}
