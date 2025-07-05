package ru.yandex.practicum.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileService {
    @Value("${storage.image.path}")
    private String imageDir;

    public String saveImage(MultipartFile image) {
        String filename = null;
        if (image != null && !image.isEmpty()) {
            filename = getFilename(image);
            Path uploadPath = Paths.get(imageDir);
            Path filePath = uploadPath.resolve(filename);
            try {
                Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return filename;
    }

    private String getFilename(MultipartFile image) {
        String fileExtension = image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf("."));
        return UUID.randomUUID() + fileExtension;
    }
}
