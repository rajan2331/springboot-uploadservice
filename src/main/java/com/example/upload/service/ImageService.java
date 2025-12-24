package com.example.upload.service;

import com.example.upload.dto.ImageProcessingEvent;
import com.example.upload.dto.ImageResponse;
import com.example.upload.dto.UploadRequest;
import com.example.upload.dto.UploadResponse;
import com.example.upload.entity.Image;
import com.example.upload.repository.ImageRepository;
import com.example.upload.service.S3Service.PresignedUrlResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {

    private final ImageRepository imageRepository;
    private final S3Service s3Service;
    private final KafkaProducerService kafkaProducerService;

    @Value("${aws.s3.presigned-url-expiration-minutes}")
    private int expirationMinutes;

    /**
     * Initiate image upload - generates presigned URL and stores metadata
     */
    @Transactional
    public UploadResponse initiateUpload(UploadRequest request) {
        log.info("Initiating upload for user: {}, file: {}", request.getUserId(), request.getFileName());

        // Generate presigned URL
        PresignedUrlResult urlResult = s3Service.generatePresignedUploadUrl(
                request.getUserId(),
                request.getFileName(),
                request.getContentType()
        );

        // Create image metadata record
        String imageId = UUID.randomUUID().toString();
        Image image = new Image();
        image.setImageId(imageId);
        image.setUserId(request.getUserId());
        image.setFileName(request.getFileName());
        image.setS3Key(urlResult.s3Key());
        image.setCaption(request.getCaption());
        image.setFileSize(request.getFileSize());
        image.setContentType(request.getContentType());
        image.setStatus(Image.ProcessingStatus.PENDING);

        imageRepository.save(image);
        log.info("Image metadata saved: imageId={}", imageId);

        return new UploadResponse(
                imageId,
                urlResult.presignedUrl(),
                urlResult.s3Key(),
                expirationMinutes
        );
    }

    /**
     * Confirm upload completion and trigger async processing via Kafka
     */
    @Transactional
    public void confirmUpload(String imageId) {
        log.info("Confirming upload for imageId: {}", imageId);

        Image image = imageRepository.findByImageId(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found: " + imageId));

        // Send to Kafka for async processing
        ImageProcessingEvent event = new ImageProcessingEvent(
                image.getImageId(),
                image.getUserId(),
                image.getS3Key(),
                image.getFileName(),
                image.getContentType()
        );

        kafkaProducerService.sendImageProcessingEvent(event);
        log.info("Upload confirmed and processing event sent: imageId={}", imageId);
    }

    /**
     * Get image by ID
     */
    public ImageResponse getImage(String imageId) {
        Image image = imageRepository.findByImageId(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found: " + imageId));
        return mapToResponse(image);
    }

    /**
     * Get all images for a user
     */
    public List<ImageResponse> getUserImages(String userId) {
        return imageRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update image processing status
     */
    @Transactional
    public void updateImageStatus(String imageId, Image.ProcessingStatus status, String thumbnailUrl) {
        Image image = imageRepository.findByImageId(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found: " + imageId));
        
        image.setStatus(status);
        if (thumbnailUrl != null) {
            image.setThumbnailUrl(thumbnailUrl);
        }
        
        imageRepository.save(image);
        log.info("Image status updated: imageId={}, status={}", imageId, status);
    }

    private ImageResponse mapToResponse(Image image) {
        return new ImageResponse(
                image.getImageId(),
                image.getUserId(),
                image.getFileName(),
                image.getS3Key(),
                image.getCaption(),
                image.getStatus().toString(),
                image.getThumbnailUrl(),
                image.getCreatedAt().toString()
        );
    }
}