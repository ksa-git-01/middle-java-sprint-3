package ru.yandex.practicum.dto;

import java.time.LocalDateTime;

public record CommentItemDto(Long id,
                             String content,
                             LocalDateTime createdAt,
                             LocalDateTime updatedAt) {
}
