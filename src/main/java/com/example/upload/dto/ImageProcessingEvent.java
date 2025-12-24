package com.example.upload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageProcessingEvent {
    private String imageId;
    private String userId;
    private String s3Key;
    private String fileName;
    private String contentType;
}
