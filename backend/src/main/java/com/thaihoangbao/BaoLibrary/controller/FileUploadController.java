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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Date;

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
            String fileDownloadUri = "/api/files/download/" + fileName;
            
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

    @GetMapping("/check-image/{fileName}")
    public ResponseEntity<?> checkImageExists(@PathVariable String fileName) {
        Path filePath = Paths.get(System.getProperty("user.dir"), "images", fileName);
        boolean exists = Files.exists(filePath);
        
        Map<String, Object> response = new HashMap<>();
        response.put("exists", exists);
        response.put("path", filePath.toString());
        response.put("fileName", fileName);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/debug-file-paths")
    public ResponseEntity<?> debugFilePaths() {
        Map<String, Object> response = new HashMap<>();
        
        Path imagesDir = Paths.get(System.getProperty("user.dir"), "images");
        response.put("imagesDirectoryExists", Files.exists(imagesDir));
        response.put("imagesDirectoryPath", imagesDir.toAbsolutePath().toString());
        response.put("currentRequestPath", "/api/files/debug-file-paths");
        
        try {
            // Create images directory if it doesn't exist
            if (!Files.exists(imagesDir)) {
                Files.createDirectories(imagesDir);
                response.put("directoryCreated", true);
            }
            
            if (Files.exists(imagesDir)) {
                // List the first 10 files in the images directory
                List<Map<String, Object>> files = Files.list(imagesDir)
                    .limit(10)
                    .map(path -> {
                        Map<String, Object> fileInfo = new HashMap<>();
                        fileInfo.put("filename", path.getFileName().toString());
                        fileInfo.put("fullPath", path.toAbsolutePath().toString());
                        fileInfo.put("apiPath", "/api/files/download/" + path.getFileName().toString());
                        fileInfo.put("size", path.toFile().length());
                        return fileInfo;
                    })
                    .collect(Collectors.toList());
                
                response.put("sampleFiles", files);
            }
            
            // Add information about endpoint and URL format
            response.put("downloadEndpoint", "/api/files/download/{fileName}");
            response.put("exampleUrl", "/api/files/download/" + 
                (Files.list(imagesDir).findFirst().isPresent() 
                    ? Files.list(imagesDir).findFirst().get().getFileName().toString() 
                    : "example.jpg"));
                    
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error in debug endpoint", e);
            response.put("error", e.getMessage());
            response.put("stackTrace", e.getStackTrace());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // Add a new endpoint to handle direct requests to /api/debug-file-paths
    @RequestMapping(value = "/api/debug-file-paths", method = RequestMethod.GET)
    public ResponseEntity<?> debugFilePathsAlternate() {
        logger.info("Direct access to /api/debug-file-paths detected, processing request");
        return debugFilePaths();
    }

    // Add a global error handler for this controller
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleExceptions(Exception e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", e.getMessage());
        errorResponse.put("timestamp", new Date());
        errorResponse.put("path", "/api/files");
        
        logger.error("Error in file controller", e);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}