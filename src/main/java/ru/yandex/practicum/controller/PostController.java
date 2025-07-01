package ru.yandex.practicum.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class PostController {
    @GetMapping("/posts")
    public String getPosts(Model model) {
        List<Post> posts = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {  // Начинаем с 1, а не 0
            posts.add(new Post(
                    "Название поста " + i,
                    "image" + i + ".jpg",
                    "Краткое содержание поста номер " + i,
                    "тег" + i + ", общий",
                    i * 2,
                    i * 5
            ));
        }
        model.addAttribute("posts", posts);
        return "posts";
    }

    public static record Post(String title, String imageFilename, String shortContent,
                              String tags, Integer commentsCount, Integer likesCount) {
    }
}