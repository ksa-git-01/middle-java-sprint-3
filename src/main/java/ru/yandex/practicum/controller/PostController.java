package ru.yandex.practicum.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.dto.*;
import ru.yandex.practicum.service.CommentService;
import ru.yandex.practicum.service.FileService;
import ru.yandex.practicum.service.PostService;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Controller
public class PostController {
    private final PostService postService;
    private final CommentService commentService;
    private final FileService fileService;

    public PostController(PostService postService, CommentService commentService, FileService fileService) {
        this.postService = postService;
        this.commentService = commentService;
        this.fileService = fileService;
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

    @GetMapping("/posts/add")
    public String showAddPostForm(Model model) {
        return "add-post";
    }

    @GetMapping("/posts/{postId}/edit")
    public String showEditPostForm(@PathVariable(name = "postId") Long postId,
                                   Model model) {
        PostItemDto post = postService.getPostItem(postId);
        model.addAttribute("post", post);
        return "add-post";
    }

    @PostMapping("/posts")
    @ResponseBody
    public void addPost(@RequestParam("title") String title,
                        @RequestParam("content") String content,
                        @RequestParam("tags") String tags,
                        @RequestParam(value = "image", required = false) MultipartFile image) {
        String filename = fileService.saveImage(image);
        List<String> tagList = convertTagsToTagList(tags);
        postService.createPost(new CreatePostRequestDto(title,
                content,
                filename,
                tagList));
    }

    @PutMapping("/posts/{postId}")
    @ResponseBody
    public void editPost(@PathVariable(name = "postId") Long postId,
                         @RequestParam("title") String title,
                         @RequestParam("content") String content,
                         @RequestParam("tags") String tags,
                         @RequestParam(value = "image", required = false) MultipartFile image) {
        String filename = fileService.saveImage(image);
        List<String> tagList = convertTagsToTagList(tags);
        postService.editPost(new EditPostRequestDto(postId,
                title,
                content,
                filename,
                tagList));
    }

    private List<String> convertTagsToTagList(String tags) {
        return Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
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

    @DeleteMapping("/posts/{postId}")
    @ResponseBody
    public void deletePost(@PathVariable(name = "postId") Long postId) {
        postService.deletePost(postId);
    }

    @PostMapping("/posts/{postId}/comment")
    @ResponseBody
    public void addComment(@PathVariable("postId") Long postId,
                           @RequestBody CommentRequestDto request) {
        if (request.content() == null || request.content().trim().isEmpty()) {
            throw new IllegalArgumentException("Содержимое комментария не может быть пустым");
        }

        postService.addCommentToPost(postId, request.content().trim());
    }

    @PostMapping("/posts/{postId}/like")
    @ResponseBody
    public void addLike(@PathVariable("postId") Long postId) {
        postService.addLikeToPost(postId);
    }
}