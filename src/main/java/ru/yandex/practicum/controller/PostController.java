package ru.yandex.practicum.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.dto.CommentItemDto;
import ru.yandex.practicum.dto.PostItemDto;
import ru.yandex.practicum.dto.PostListItemDto;
import ru.yandex.practicum.service.CommentService;
import ru.yandex.practicum.service.PostService;

import java.util.List;
import java.util.Set;

@Controller
public class PostController {
    private final PostService postService;
    private final CommentService commentService;

    public PostController(PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

    @GetMapping("/posts")
    public String getPosts(@RequestParam(name = "page", defaultValue = "0") int page,
                           @RequestParam(name = "size", defaultValue = "10") int size,
                           @RequestParam(name = "tag", required = false) String tag,
                           Model model) {
        if (page < 0) {
            page = 0;
        }
        if (!Set.of(10, 20, 50).contains(size)) {
            size = 10;
        }
        List<PostListItemDto> postListItems = postService.getPostListItems(page, size, tag);

        model.addAttribute("posts", postListItems);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("tag", tag);
        return "posts";
    }

    @GetMapping("/posts/{postId}")
    public String getPosts(@PathVariable(name = "postId") Long postId,
                           Model model) {
        PostItemDto postItem = postService.getPostItem(postId);
        List<CommentItemDto> commentItemList = commentService.getCommentsByPostId(postId);

        model.addAttribute("post", postItem);
        model.addAttribute("comments", commentItemList);
        return "post";
    }
}