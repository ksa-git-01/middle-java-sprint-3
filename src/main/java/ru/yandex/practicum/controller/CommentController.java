package ru.yandex.practicum.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.service.CommentService;

@RestController
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @DeleteMapping("/comments/{commentId}")
    public void deleteComment(@PathVariable(name = "commentId") Long commentId) {
        commentService.deleteCommentByCommentId(commentId);
    }
}
