package com.example.upload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageResponse {
    private String imageId;
    private String userId;
    private String fileName;
    private String s3Key;
    private String caption;
    private String status;
    private String thumbnailUrl;
    private String createdAt;
}
