package com.thaihoangbao.BaoLibrary.controller;

import com.thaihoangbao.BaoLibrary.dto.FileUploadResponse;
import com.thaihoangbao.BaoLibrary.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {
    
    @Autowired
    private FileStorageService fileStorageService;
    
    @PostMapping("/upload")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    public ResponseEntity<FileUploadResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        String fileName = fileStorageService.storeFile(file);
        String fileDownloadUri = "/api/files/download/" + fileName;
        
        FileUploadResponse response = new FileUploadResponse(
                fileName,
                fileDownloadUri,
                file.getContentType(),
                file.getSize()
        );
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @GetMapping("/download/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileName) {
        byte[] fileContent = fileStorageService.getFile(fileName);
        
        // Determine content type based on file extension
        String contentType = "application/octet-stream";
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            contentType = MediaType.IMAGE_JPEG_VALUE;
        } else if (fileName.endsWith(".png")) {
            contentType = MediaType.IMAGE_PNG_VALUE;
        } else if (fileName.endsWith(".pdf")) {
            contentType = "application/pdf";
        }
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(fileContent);
    }
}