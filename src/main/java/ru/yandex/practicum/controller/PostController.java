package ru.yandex.practicum.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.dto.PostListItemDto;
import ru.yandex.practicum.service.PostService;

import java.util.List;
import java.util.Set;

@Controller
public class PostController {
    private final PostService service;

    public PostController(PostService service) {
        this.service = service;
    }

    @GetMapping("/posts")
    public String getPosts(@RequestParam(name = "page", defaultValue = "0") int page,
                           @RequestParam(name = "size", defaultValue = "10") int size,
                           Model model) {
        if (page < 0) {
            page = 0;
        }
        if (!Set.of(10, 20, 50).contains(size)) {
            size = 10;
        }
        List<PostListItemDto> postListItems = service.getPostListItems(page, size);

        model.addAttribute("posts", postListItems);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        return "posts";
    }
}