package com.thaihoangbao.BaoLibrary.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/proxy")
public class ImageProxyController {
    
    private static final Logger logger = LoggerFactory.getLogger(ImageProxyController.class);
    private final RestTemplate restTemplate;
    
    // Pattern to extract Google Drive file ID
    private static final Pattern GOOGLE_DRIVE_PATTERN = Pattern.compile("[-\\w]{25,}");
    
    public ImageProxyController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    @GetMapping("/image")
    @PreAuthorize("permitAll()")
    public ResponseEntity<byte[]> proxyImage(@RequestParam String url) {
        try {
            // Special handling for Google Drive URLs
            if (url.contains("drive.google.com")) {
                url = convertGoogleDriveUrl(url);
            }
            
            logger.info("Proxying image from URL: {}", url);
            
            // Set timeout options for the template
            // This requires configuration in a @Bean method if not using constructor injection
            
            // Fetch the image from the external URL
            ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);
            
            // Return the image with appropriate content type
            HttpHeaders headers = new HttpHeaders();
            if (response.getHeaders().getContentType() != null) {
                headers.setContentType(response.getHeaders().getContentType());
            } else {
                // Fallback to a generic image type
                headers.setContentType(MediaType.IMAGE_JPEG);
            }
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(response.getBody());
        } catch (Exception e) {
            logger.error("Error proxying image from URL: {}", url, e);
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Converts various Google Drive URL formats to a direct download URL
     */
    private String convertGoogleDriveUrl(String url) {
        // Extract file ID
        Matcher matcher = GOOGLE_DRIVE_PATTERN.matcher(url);
        if (matcher.find()) {
            String fileId = matcher.group();
            logger.info("Extracted Google Drive file ID: {}", fileId);
            return "https://drive.google.com/uc?export=view&id=" + fileId;
        }
        return url;
    }
}