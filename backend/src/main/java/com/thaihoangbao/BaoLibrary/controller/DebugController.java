package com.thaihoangbao.BaoLibrary.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class DebugController {
    
    private static final Logger logger = LoggerFactory.getLogger(DebugController.class);
    
    @Autowired
    private FileUploadController fileUploadController;
    
    @GetMapping("/debug-file-paths")
    public ResponseEntity<?> debugFilePaths() {
        logger.info("Direct access to /api/debug-file-paths endpoint");
        
        Map<String, Object> response = new HashMap<>();
        
        Path imagesDir = Paths.get(System.getProperty("user.dir"), "images");
        response.put("imagesDirectoryExists", Files.exists(imagesDir));
        response.put("imagesDirectoryPath", imagesDir.toAbsolutePath().toString());
        response.put("currentRequestPath", "/api/debug-file-paths");
        response.put("note", "This is the direct endpoint. Use /api/files/debug-file-paths for the standard endpoint.");
        
        try {
            // Create directory if it doesn't exist
            if (!Files.exists(imagesDir)) {
                Files.createDirectories(imagesDir);
                response.put("directoryCreated", true);
            }
            
            if (Files.exists(imagesDir)) {
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
            
            // Add information about endpoints
            response.put("downloadEndpoint", "/api/files/download/{fileName}");
            response.put("standardDebugEndpoint", "/api/files/debug-file-paths");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error in direct debug endpoint", e);
            response.put("error", e.getMessage());
            response.put("stackTrace", e.getStackTrace());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
