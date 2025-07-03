package ru.yandex.practicum.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.CommentRequestDto;
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

    @PutMapping("/comments/{commentId}")
    public void updateComment(@PathVariable(name = "commentId") Long commentId,
                              @RequestBody CommentRequestDto request) {
        if (request.content() == null || request.content().trim().isEmpty()) {
            throw new IllegalArgumentException("Содержимое комментария не может быть пустым");
        }
        commentService.updateCommentById(commentId, request.content());
    }
}
