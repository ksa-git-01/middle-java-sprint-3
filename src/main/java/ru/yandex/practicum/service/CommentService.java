package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.dao.CommentDao;
import ru.yandex.practicum.dto.CommentItemDto;
import ru.yandex.practicum.model.Comment;

import java.util.List;

@Service
public class CommentService {
    private final CommentDao commentDao;

    public CommentService(CommentDao commentDao) {
        this.commentDao = commentDao;
    }

    public List<CommentItemDto> getCommentsByPostId(Long postId) {
        return commentDao.findAllByPostId(postId).stream()
                .map(this::mapToCommentItemDto)
                .toList();
    }

    public void deleteCommentByCommentId(Long commentId) {
        commentDao.deleteCommentByCommentId(commentId);
    }

    private CommentItemDto mapToCommentItemDto(Comment comment) {
        return new CommentItemDto(comment.id(),
                comment.content(),
                comment.createdAt(),
                comment.updatedAt());
    }
}
