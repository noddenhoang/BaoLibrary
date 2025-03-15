package com.thaihoangbao.BaoLibrary.service.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.thaihoangbao.BaoLibrary.exception.FileStorageException;
import com.thaihoangbao.BaoLibrary.exception.ResourceNotFoundException;
import com.thaihoangbao.BaoLibrary.service.FileStorageService;

@Service
public class FileStorageServiceImpl implements FileStorageService {
    
    private final Path fileStorageLocation;
    private static final Logger logger = LoggerFactory.getLogger(FileStorageServiceImpl.class);
    
    public FileStorageServiceImpl() {
        // Create the directory if it doesn't exist
        this.fileStorageLocation = Paths.get(System.getProperty("user.dir"), "images")
            .toAbsolutePath().normalize();
        
        try {
            logger.info("File storage location: {}", this.fileStorageLocation);
            
            if (!Files.exists(fileStorageLocation)) {
                logger.info("Creating directory: {}", fileStorageLocation);
                Files.createDirectories(this.fileStorageLocation);
            } else {
                logger.info("Directory already exists");
            }
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }
    
    @Override
    public String storeFile(MultipartFile file) {
        // Normalize file name
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        logger.info("Storing file: {}", originalFileName);
        
        try {
            // Check if the file's name contains invalid characters
            if (originalFileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + originalFileName);
            }
            
            // Generate a unique file name to avoid conflicts
            String fileExtension = "";
            if (originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
            logger.info("Generated unique filename: {}", uniqueFileName);
            
            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(uniqueFileName);
            logger.info("Target location: {}", targetLocation);
            
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            // Verify file was created
            if (Files.exists(targetLocation)) {
                logger.info("File stored successfully at {}", targetLocation);
            } else {
                logger.warn("File doesn't exist after copying: {}", targetLocation);
            }
            
            return uniqueFileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + originalFileName + ". Please try again!", ex);
        }
    }
    
    @Override
    public byte[] getFile(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            logger.info("Looking for file at path: {}", filePath);
            
            if (!Files.exists(filePath)) {
                logger.warn("File not found: {}", filePath);
                throw new ResourceNotFoundException("File not found: " + fileName);
            }
            
            Resource resource = new UrlResource(filePath.toUri());
            logger.info("Resource URI: {}", resource.getURI());
            
            if (resource.exists()) {
                logger.info("File found, reading bytes");
                return Files.readAllBytes(filePath);
            } else {
                logger.warn("Resource does not exist: {}", resource.getURI());
                throw new ResourceNotFoundException("File not found: " + fileName);
            }
        } catch (MalformedURLException ex) {
            logger.error("MalformedURLException for file: {}", fileName, ex);
            throw new ResourceNotFoundException("File not found: " + fileName, ex);
        } catch (IOException ex) {
            logger.error("IOException reading file: {}", fileName, ex);
            throw new FileStorageException("Could not read file: " + fileName, ex);
        }
    }
}
