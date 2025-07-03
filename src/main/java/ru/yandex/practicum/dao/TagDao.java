package ru.yandex.practicum.dao;

import ru.yandex.practicum.dto.CreatePostRequestDto;

import java.util.List;
import java.util.Map;

public interface TagDao {
    Map<Long, List<String>> findTagsByPostIds(List<Long> postIds);

    List<String> findTagsByPostId(Long postId);

    void createTags(List<String> tags);

    void createPostTags(Long postId, CreatePostRequestDto createPostRequestDto);
}
