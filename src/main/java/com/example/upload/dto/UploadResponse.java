package com.example.upload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadResponse {
    private String imageId;
    private String presignedUrl;
    private String s3Key;
    private int expirationMinutes;
}
