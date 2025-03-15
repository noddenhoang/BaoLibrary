package com.thaihoangbao.BaoLibrary.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/debug")
public class FileDebugController {
    
    private static final Logger logger = LoggerFactory.getLogger(FileDebugController.class);
    
    @GetMapping("/file-storage")
    public ResponseEntity<?> debugFileStorage() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Get the images directory path
            Path imagesPath = Paths.get(System.getProperty("user.dir"), "images")
                .toAbsolutePath().normalize();
            
            // Check if directory exists
            boolean directoryExists = Files.exists(imagesPath);
            response.put("directoryExists", directoryExists);
            response.put("directoryPath", imagesPath.toString());
            
            if (directoryExists) {
                // List files in directory
                List<String> files = Files.list(imagesPath)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());
                response.put("files", files);
                response.put("fileCount", files.size());
            }
            
            // Check placeholder image
            File placeholder = new File("src/main/resources/static/placeholder-book.png");
            response.put("placeholderExists", placeholder.exists());
            if (placeholder.exists()) {
                response.put("placeholderPath", placeholder.getAbsolutePath());
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error in debug endpoint", e);
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
