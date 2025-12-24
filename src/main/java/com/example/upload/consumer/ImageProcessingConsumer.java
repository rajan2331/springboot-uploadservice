package com.example.upload.consumer;

import com.example.upload.dto.ImageProcessingEvent;
import com.example.upload.entity.Image;
import com.example.upload.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer that processes uploaded images asynchronously
 * In a real system, this would:
 * - Generate thumbnails
 * - Apply filters/transformations
 * - Extract metadata (EXIF data)
 * - Run content moderation
 * - Generate different image sizes
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ImageProcessingConsumer {

    private final ImageService imageService;

    @KafkaListener(
            topics = "${kafka.topics.image-processing}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void processImage(ImageProcessingEvent event) {
        log.info("Received image processing event: imageId={}, s3Key={}", 
                event.getImageId(), event.getS3Key());

        try {
            // Update status to PROCESSING
            imageService.updateImageStatus(event.getImageId(), Image.ProcessingStatus.PROCESSING, null);

            // Simulate image processing operations
            processImageOperations(event);

            // Generate thumbnail URL (in real scenario, upload thumbnail to S3)
            String thumbnailUrl = generateThumbnailUrl(event);

            // Update status to COMPLETED
            imageService.updateImageStatus(event.getImageId(), Image.ProcessingStatus.COMPLETED, thumbnailUrl);

            log.info("Image processing completed successfully: imageId={}", event.getImageId());

        } catch (Exception e) {
            log.error("Image processing failed: imageId={}", event.getImageId(), e);
            
            // Update status to FAILED
            imageService.updateImageStatus(event.getImageId(), Image.ProcessingStatus.FAILED, null);
        }
    }

    /**
     * Simulate image processing operations
     * In production, this would:
     * 1. Download image from S3
     * 2. Generate thumbnails (multiple sizes)
     * 3. Apply compression/optimization
     * 4. Extract EXIF metadata
     * 5. Run ML models for content moderation
     * 6. Upload processed images back to S3
     */
    private void processImageOperations(ImageProcessingEvent event) {
        log.info("Processing image operations for: {}", event.getImageId());
        
        try {
            // Simulate processing time
            Thread.sleep(2000);
            
            // Here you would:
            // - Use AWS SDK to download from S3
            // - Use libraries like Thumbnailator, ImageMagick for processing
            // - Upload processed images back to S3
            // - Store URLs in database
            
            log.info("Generated thumbnail for imageId: {}", event.getImageId());
            log.info("Applied compression for imageId: {}", event.getImageId());
            log.info("Extracted metadata for imageId: {}", event.getImageId());
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Image processing interrupted", e);
        }
    }

    /**
     * Generate thumbnail URL
     * In production, this would be the actual S3 URL of the generated thumbnail
     */
    private String generateThumbnailUrl(ImageProcessingEvent event) {
        return String.format("https://my-bucket.s3.amazonaws.com/thumbnails/%s-thumb.jpg", 
                event.getImageId());
    }
}