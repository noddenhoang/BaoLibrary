package com.thaihoangbao.BaoLibrary.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173") // URL của frontend
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Get absolute path to images directory
        String imagesPath = Paths.get(System.getProperty("user.dir"), "images").toAbsolutePath().toString();
        
        // Ensure path ends with separator
        if (!imagesPath.endsWith(System.getProperty("file.separator"))) {
            imagesPath += System.getProperty("file.separator");
        }
        
        // Create directory if it doesn't exist
        try {
            Files.createDirectories(Paths.get(imagesPath));
            logger.info("Images directory ensured at: " + imagesPath);
        } catch (Exception e) {
            logger.error("Failed to create images directory: " + e.getMessage(), e);
        }
        
        // Configure both access paths
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + imagesPath);

        registry.addResourceHandler("/static/images/**")
                .addResourceLocations("file:" + imagesPath);

        // Make sure the API paths are explicitly handled as non-static resources
        registry.addResourceHandler("/api/**")
                .addResourceLocations("classpath:/META-INF/resources/")  
                .resourceChain(false);

        // Specifically handle the debug endpoint if it's being accessed directly
        registry.addResourceHandler("/api/debug-file-paths")
                .addResourceLocations("classpath:/META-INF/resources/")
                .resourceChain(false);
    }
}