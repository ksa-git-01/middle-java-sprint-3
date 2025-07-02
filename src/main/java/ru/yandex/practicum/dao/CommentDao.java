package ru.yandex.practicum.dao;

import java.util.List;
import java.util.Map;

public interface CommentDao {
    Map<Long, Integer> findCommentCountByPostIds(List<Long> postIds);
}
