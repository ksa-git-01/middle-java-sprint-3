package ru.yandex.practicum.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.yandex.practicum.service.FileService;

@Controller
public class ImageController {
    private final FileService fileService;

    public ImageController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/images/{filename}")
    @ResponseBody
    public ResponseEntity<Resource> getImage(@PathVariable(name = "filename") String filename) {
        try {
            Resource resource = fileService.getImageResource(filename);

            if (resource == null) {
                return ResponseEntity.notFound().build();
            }

            String contentType = fileService.getImageContentType(filename);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}