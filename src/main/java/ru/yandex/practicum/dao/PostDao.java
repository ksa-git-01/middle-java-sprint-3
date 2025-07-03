package ru.yandex.practicum.dao;

import ru.yandex.practicum.dto.CreatePostRequestDto;
import ru.yandex.practicum.model.Post;

import java.util.List;

public interface PostDao {
    List<Post> findAllWithPagination(Integer page, Integer size);

    List<Post> findAllByTagWithPagination(Integer page, Integer size, String tag);

    Post getPostByPostId(Long postId);

    void deletePost(Long postId);

    void addLikeToPost(Long postId);

    Long createPost(CreatePostRequestDto createPostRequestDto);
}
