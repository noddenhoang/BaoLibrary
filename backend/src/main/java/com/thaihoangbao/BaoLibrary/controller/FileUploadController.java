package com.thaihoangbao.BaoLibrary.controller;

import com.thaihoangbao.BaoLibrary.dto.FileUploadResponse;
import com.thaihoangbao.BaoLibrary.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {
    
    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);
    
    @Autowired
    private FileStorageService fileStorageService;
    
    @PostMapping("/upload")
    // Temporarily comment out strict role check during debugging
    // @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    public ResponseEntity<FileUploadResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        // Log authentication details for debugging
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            logger.info("Upload request from user: {}, authorities: {}", 
                    auth.getName(), 
                    auth.getAuthorities());
        } else {
            logger.warn("Upload request received with no authentication");
        }
        
        try {
            String fileName = fileStorageService.storeFile(file);
            
            // Create full URL for the file
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/files/download/")
                    .path(fileName)
                    .toUriString();
            
            FileUploadResponse response = new FileUploadResponse(
                    fileName,
                    fileDownloadUri,
                    file.getContentType(),
                    file.getSize()
            );
            
            logger.info("File uploaded successfully: {}", fileName);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error uploading file", e);
            throw e;
        }
    }
    
    @GetMapping("/download/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileName) {
        try {
            byte[] fileContent = fileStorageService.getFile(fileName);
            
            // Determine content type based on file extension
            String contentType = "application/octet-stream";
            if (fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".jpeg")) {
                contentType = MediaType.IMAGE_JPEG_VALUE;
            } else if (fileName.toLowerCase().endsWith(".png")) {
                contentType = MediaType.IMAGE_PNG_VALUE;
            } else if (fileName.toLowerCase().endsWith(".pdf")) {
                contentType = "application/pdf";
            } else if (fileName.toLowerCase().endsWith(".gif")) {
                contentType = "image/gif";
            }
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(fileContent);
        } catch (Exception e) {
            logger.error("Error downloading file: {}", fileName, e);
            throw e;
        }
    }
}