package com.thaihoangbao.BaoLibrary.service.impl;

import com.thaihoangbao.BaoLibrary.exception.InvalidRequestException;
import com.thaihoangbao.BaoLibrary.service.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {
    
    private final Path fileStorageLocation;
    
    public FileStorageServiceImpl() {
        // Create upload directory if it doesn't exist
        String uploadDir = System.getProperty("user.home") + File.separator + "library-uploads";
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException ex) {
            throw new RuntimeException("Could not create the directory for file storage", ex);
        }
    }
    
    @Override
    public String storeFile(MultipartFile file) {
        // Check if the file is empty
        if (file.isEmpty()) {
            throw new InvalidRequestException("Failed to store empty file");
        }
        
        // Generate unique filename to prevent overwriting
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = "";
        
        int dotIndex = originalFilename.lastIndexOf(".");
        if (dotIndex > 0) {
            fileExtension = originalFilename.substring(dotIndex);
        }
        
        String newFilename = UUID.randomUUID().toString() + fileExtension;
        
        try {
            // Copy file to the target location
            Path targetLocation = this.fileStorageLocation.resolve(newFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            return newFilename;
        } catch (IOException ex) {
            throw new RuntimeException("Failed to store file " + originalFilename, ex);
        }
    }
    
    @Override
    public byte[] getFile(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            return Files.readAllBytes(filePath);
        } catch (IOException ex) {
            throw new RuntimeException("File not found: " + fileName, ex);
        }
    }
}
