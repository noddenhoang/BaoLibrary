package com.thaihoangbao.BaoLibrary.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.thaihoangbao.BaoLibrary.exception.FileStorageException;
import com.thaihoangbao.BaoLibrary.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryStorageServiceImpl implements FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(CloudinaryStorageServiceImpl.class);
    
    @Autowired
    private Cloudinary cloudinary;

    @Override
    public Map<String, String> uploadFile(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new FileStorageException("Không thể tải lên tệp trống");
            }
            
            logger.info("Uploading file to Cloudinary: {}", file.getOriginalFilename());
            
            // Tạo folder và unique public_id
            String folder = "bao_library";
            String uniqueFileName = UUID.randomUUID().toString();
            
            // Upload tới Cloudinary
            Map<String, Object> params = ObjectUtils.asMap(
                "public_id", folder + "/" + uniqueFileName,
                "overwrite", true,
                "resource_type", "auto"
            );
            
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
            
            // Trả về map chứa thông tin URL
            Map<String, String> result = new HashMap<>();
            result.put("publicId", uploadResult.get("public_id").toString());
            result.put("url", uploadResult.get("secure_url").toString());
            result.put("format", uploadResult.get("format").toString());
            result.put("size", uploadResult.get("bytes").toString());
            
            logger.info("File uploaded successfully: {}", result.get("url"));
            
            return result;
        } catch (IOException e) {
            logger.error("Error uploading file to Cloudinary", e);
            throw new FileStorageException("Không thể tải lên tệp: " + e.getMessage());
        }
    }
}