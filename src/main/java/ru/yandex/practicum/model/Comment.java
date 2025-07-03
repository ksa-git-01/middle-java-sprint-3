package ru.yandex.practicum.model;

import java.time.LocalDateTime;

public record Comment(Long id,
                      Long postId,
                      String content,
                      LocalDateTime createdAt,
                      LocalDateTime updatedAt) {
}
