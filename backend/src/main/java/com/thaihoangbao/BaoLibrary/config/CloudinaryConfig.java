package com.thaihoangbao.BaoLibrary.config;

import com.cloudinary.Cloudinary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {
    private static final Logger logger = LoggerFactory.getLogger(CloudinaryConfig.class);

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    @Bean
    public Cloudinary cloudinary() {
        logger.info("Cloudinary Configuration from application.properties:");
        logger.info("Cloud Name: {}", cloudName);
        if (apiKey != null && apiKey.length() > 6) {
            logger.info("API Key: {}...{} (length: {})", 
                    apiKey.substring(0, 3), 
                    apiKey.substring(apiKey.length() - 3), 
                    apiKey.length());
        } else {
            logger.warn("API Key is invalid: {}", apiKey);
        }

        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", cloudName);
        config.put("api_key", apiKey);
        config.put("api_secret", apiSecret);
        config.put("secure", "true");
        
        return new Cloudinary(config);
    }
}