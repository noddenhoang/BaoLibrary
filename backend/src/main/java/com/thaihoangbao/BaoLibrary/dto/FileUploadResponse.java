package com.thaihoangbao.BaoLibrary.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileUploadResponse {
    private String publicId;
    private String fileUrl;
    private String fileType;
    private long size;
}
