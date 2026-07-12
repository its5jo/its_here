package com.spring.its_here.infrastructure.storage;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStorage {
    String store(MultipartFile file);
    void delete(String fileUrl);
}
