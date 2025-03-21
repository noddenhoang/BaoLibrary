package com.thaihoangbao.BaoLibrary.controller;

import com.thaihoangbao.BaoLibrary.dto.FileUploadResponse;
import com.thaihoangbao.BaoLibrary.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {
    
    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);
    
    @Autowired
    private FileStorageService fileStorageService;
    
    @PostMapping("/upload")
    @PreAuthorize("hasAuthority('admin') or hasAuthority('manager')")
    public ResponseEntity<FileUploadResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        // Log authentication details for debugging
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            logger.info("Upload request from user: {}, authorities: {}", 
                    auth.getName(), 
                    auth.getAuthorities());
        }
        
        try {
            Map<String, String> uploadResult = fileStorageService.uploadFile(file);
            
            FileUploadResponse response = new FileUploadResponse(
                    uploadResult.get("publicId"),
                    uploadResult.get("url"),
                    file.getContentType(),
                    Long.parseLong(uploadResult.get("size"))
            );
            
            logger.info("File uploaded successfully to Cloudinary: {}", uploadResult.get("url"));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error uploading file", e);
            throw e;
        }
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleExceptions(Exception e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", e.getMessage());
        
        logger.error("Error in file controller", e);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}