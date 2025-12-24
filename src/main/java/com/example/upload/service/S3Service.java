package com.example.upload.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.presigned-url-expiration-minutes}")
    private int expirationMinutes;

    /**
     * Generate a presigned URL for uploading an image directly to S3
     */
    public PresignedUrlResult generatePresignedUploadUrl(String userId, String fileName, String contentType) {
        // Generate unique S3 key
        String s3Key = generateS3Key(userId, fileName);
        
        log.info("Generating presigned URL for s3Key: {}", s3Key);

        // Create PutObjectRequest
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .contentType(contentType)
                .build();

        // Create presigned request with expiration
        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(expirationMinutes))
                .putObjectRequest(putObjectRequest)
                .build();

        // Generate presigned URL
        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
        String presignedUrl = presignedRequest.url().toString();

        log.info("Generated presigned URL successfully");

        return new PresignedUrlResult(presignedUrl, s3Key);
    }

    /**
     * Generate unique S3 key for the image
     * Format: uploads/{userId}/{timestamp}_{uuid}_{fileName}
     */
    private String generateS3Key(String userId, String fileName) {
        String uuid = UUID.randomUUID().toString();
        long timestamp = System.currentTimeMillis();
        String sanitizedFileName = sanitizeFileName(fileName);
        return String.format("uploads/%s/%d_%s_%s", userId, timestamp, uuid, sanitizedFileName);
    }

    /**
     * Sanitize file name to remove special characters
     */
    private String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    /**
     * Result object containing presigned URL and S3 key
     */
    public record PresignedUrlResult(String presignedUrl, String s3Key) {}
}