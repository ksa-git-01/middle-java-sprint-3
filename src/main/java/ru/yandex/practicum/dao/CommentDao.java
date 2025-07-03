package ru.yandex.practicum.dao;

import ru.yandex.practicum.model.Comment;

import java.util.List;
import java.util.Map;

public interface CommentDao {
    Map<Long, Integer> findCommentCountByPostIds(List<Long> postIds);

    List<Comment> findAllByPostId(Long postId);

    void deleteCommentByCommentId(Long commentId);

    void addCommentToPost(Long postId, String content);

    void updateCommentById(Long commentId, String content);
}
