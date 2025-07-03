package ru.yandex.practicum.dto;

import java.util.List;

public record PostItemDto(Long id,
                          String title,
                          String content,
                          Integer likes,
                          String filename,
                          List<String> tags) {
}
