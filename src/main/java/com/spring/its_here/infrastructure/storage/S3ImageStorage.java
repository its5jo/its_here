package com.spring.its_here.infrastructure.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
@ConditionalOnProperty(name = "storage.type", havingValue = "s3")
public class S3ImageStorage implements ImageStorage {


    @Override
    public String store(MultipartFile file) {
        return "";
    }

    @Override
    public void delete(String fileUrl) {

    }
}
