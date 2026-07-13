package com.spring.its_here.infrastructure.storage;

import com.spring.its_here.global.advice.ErrorCode;
import com.spring.its_here.global.advice.ItsHereException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Component
@ConditionalOnProperty(name = "storage.type", havingValue = "local")
public class LocalImageStorage implements ImageStorage{

    @Value("${storage.local.root-path}")
    private String localDir;

    @Override
    public String store(MultipartFile file) {
        validate(file);
        String relativePath = createRelativePath(file.getOriginalFilename());
        Path targetPath = createTargetPath(relativePath);
        save(file, targetPath);

        return buildUrl(relativePath);
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ItsHereException(ErrorCode.EMPTY_FILE);
        }
    }

    private String createRelativePath(String originalFilename) {
        String extension = getExtension(originalFilename);
        return "image/" + UUID.randomUUID() + extension;

    }

    private Path createTargetPath(String relativePath) {
        return Paths.get(localDir, relativePath).toAbsolutePath();
    }

    private void save(MultipartFile file, Path targetPath) {

        try {
            Files.createDirectories(targetPath.getParent());
            file.transferTo(targetPath.toFile());
        } catch (IOException e) {
            log.error("[LocalImageStorage] 파일 저장 실패 - {}", targetPath.toAbsolutePath(), e);
            throw new ItsHereException(ErrorCode.FILE_UPLOAD_FAILED);
        }

    }

    private String buildUrl(String relativePath) {
        return "/" + relativePath;
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.'));
    }

    @Override
    public void delete(String fileUrl) {

    }
}
