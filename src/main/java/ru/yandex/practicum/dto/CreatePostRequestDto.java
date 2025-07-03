package ru.yandex.practicum.dto;

import java.util.List;

public record CreatePostRequestDto(String title,
                                   String content,
                                   String filename,
                                   List<String> tags) {
}
