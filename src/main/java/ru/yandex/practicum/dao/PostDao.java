package ru.yandex.practicum.dao;

import ru.yandex.practicum.model.Post;

import java.util.List;

public interface PostDao {
    public List<Post> findAllWithPagination(Integer page, Integer size);
}
