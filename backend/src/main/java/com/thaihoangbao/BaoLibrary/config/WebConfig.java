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
        // Không cần tạo thư mục images nữa vì đã dùng Cloudinary
        // Chỉ để lại cấu hình cho các tài nguyên tĩnh khác nếu cần
        
        // Thêm cấu hình cho các tài nguyên tĩnh khác ở đây (nếu cần)
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
}