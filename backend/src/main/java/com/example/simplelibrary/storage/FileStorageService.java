package com.example.simplelibrary.storage;

import com.example.simplelibrary.config.AppProperties;
import com.example.simplelibrary.exception.NotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {
    private final Path baseDir;
    private final long maxSizeBytes;

    public FileStorageService(AppProperties appProperties) {
        this.baseDir = Paths.get(appProperties.getUpload().getDir()).toAbsolutePath().normalize();
        this.maxSizeBytes = appProperties.getUpload().getMaxSizeMb() * 1024L * 1024L;
    }

    public String saveBookCover(String bookId, MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        if (file.getSize() > maxSizeBytes) {
            throw new IllegalArgumentException("File too large");
        }
        String ext = "";
        String original = file.getOriginalFilename();
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf('.'));
        }
        String filename = "cover-" + bookId + "-" + UUID.randomUUID() + ext;
        Path dir = baseDir.resolve("covers");
        try {
            Files.createDirectories(dir);
            Path target = dir.resolve(filename).normalize();
            if (!target.startsWith(dir)) {
                throw new IllegalArgumentException("Invalid file path");
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
            }
            return baseDir.relativize(target).toString().replace("\\", "/");
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file");
        }
    }

    public Resource loadAsResource(String relativePath) {
        try {
            Path file = baseDir.resolve(relativePath).normalize();
            if (!file.startsWith(baseDir)) {
                throw new NotFoundException("File not found");
            }
            Resource resource = new FileSystemResource(file);
            if (!resource.exists()) {
                throw new NotFoundException("File not found");
            }
            return resource;
        } catch (Exception e) {
            throw new NotFoundException("File not found");
        }
    }
}

