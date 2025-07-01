package ru.yandex.practicum.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.yandex.practicum.dto.PostListItemDto;
import ru.yandex.practicum.service.PostService;

import java.util.List;

@Controller
public class PostController {
    private final PostService service;

    public PostController(PostService service) {
        this.service = service;
    }

    @GetMapping("/posts")
    public String getPosts(Model model) {
        List<PostListItemDto> postListItems = service.getPostListItems(0, 25);

        model.addAttribute("posts", postListItems);
        return "posts";
    }
}