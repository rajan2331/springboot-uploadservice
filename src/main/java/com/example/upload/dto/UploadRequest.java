package com.example.upload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Request to initiate upload
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadRequest {
    private String userId;
    private String fileName;
    private String contentType;
    private Long fileSize;
    private String caption;
}
