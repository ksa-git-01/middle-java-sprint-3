package ru.yandex.practicum.model;

import java.time.LocalDateTime;

public record Post(Long id,
                   String title,
                   String content,
                   Integer likes,
                   String filename,
                   LocalDateTime createdAt,
                   LocalDateTime updatedAt) {
}
