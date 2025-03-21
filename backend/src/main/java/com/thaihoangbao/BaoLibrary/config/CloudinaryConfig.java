package com.thaihoangbao.BaoLibrary.config;

import com.cloudinary.Cloudinary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
public class CloudinaryConfig {
    private static final Logger logger = LoggerFactory.getLogger(CloudinaryConfig.class);

    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = new HashMap<>();
        
        try {
            // Tìm kiếm file .env ở nhiều vị trí khác nhau
            String[] possiblePaths = {
                ".env",
                "src/main/resources/.env",
                "../.env",
                "d:/Code/BaoLib/BaoLibrary/backend/.env"
            };
            
            Properties props = new Properties();
            boolean loaded = false;
            
            for (String path : possiblePaths) {
                File file = new File(path);
                if (file.exists()) {
                    logger.info("Loading .env from: {}", file.getAbsolutePath());
                    props.load(new FileInputStream(file));
                    loaded = true;
                    break;
                }
            }
            
            if (!loaded) {
                // Fallback to hardcoded values
                logger.warn("Could not find .env file, using hardcoded values");
                config.put("cloud_name", "dsaer4xfh");
                config.put("api_key", "339512598747413");
                config.put("api_secret", "-4n7UenBz3eU7JLVl_kCCn1d018");
            } else {
                String cloudName = props.getProperty("CLOUDINARY_CLOUD_NAME");
                String apiKey = props.getProperty("CLOUDINARY_API_KEY");
                String apiSecret = props.getProperty("CLOUDINARY_API_SECRET");
                
                logger.info("Cloudinary Configuration loaded from .env:");
                logger.info("Cloud Name: {}", cloudName);
                
                config.put("cloud_name", cloudName);
                config.put("api_key", apiKey);
                config.put("api_secret", apiSecret);
            }
            
            config.put("secure", "true");
        } catch (IOException e) {
            logger.error("Error loading .env file", e);
            // Fallback to hardcoded values
            config.put("cloud_name", "dsaer4xfh");
            config.put("api_key", "339512598747413");
            config.put("api_secret", "-4n7UenBz3eU7JLVl_kCCn1d018");
            config.put("secure", "true");
        }
        
        return new Cloudinary(config);
    }
}