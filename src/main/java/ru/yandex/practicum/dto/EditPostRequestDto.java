package ru.yandex.practicum.dto;

import java.util.List;

public record EditPostRequestDto(Long id,
                                 String title,
                                 String content,
                                 String filename,
                                 List<String> tags) {
}
