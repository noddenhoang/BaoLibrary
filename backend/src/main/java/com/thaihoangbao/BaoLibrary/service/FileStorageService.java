package com.thaihoangbao.BaoLibrary.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface FileStorageService {
    Map<String, String> uploadFile(MultipartFile file);
}
