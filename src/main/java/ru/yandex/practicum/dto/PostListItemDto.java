package ru.yandex.practicum.dto;

import java.util.List;

public record PostListItemDto(Long id,
                              String title,
                              String contentPreview,
                              Integer likes,
                              String filename,
                              Integer comments,
                              List<String> tags) {
}
