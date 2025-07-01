package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.dao.PostDao;
import ru.yandex.practicum.dto.PostListItemDto;
import ru.yandex.practicum.model.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final PostDao postDao;

    public PostService(PostDao postDao) {
        this.postDao = postDao;
    }

    public List<PostListItemDto> getPostListItems(Integer page, Integer size) {
        List<PostListItemDto> postListItems = new ArrayList<>();
        List<Post> posts = postDao.findAllWithPagination(page, size);
        if (posts.isEmpty()) {
            return postListItems;
        }
        return posts.stream()
                .map(this::mapToPostListItemDto)
                .map(this::addDefaultValues)
                .collect(Collectors.toList());
    }

    private PostListItemDto mapToPostListItemDto(Post post) {
        return new PostListItemDto(post.id(),
                post.title(),
                truncateContent(post.content()),
                post.likes(),
                post.filename()
        );
    }

    private String truncateContent(String content) {
        if (content == null) return "";
        return content.lines()
                .limit(3)
                .collect(Collectors.joining("\n"));
    }

    private PostListItemDto addDefaultValues(PostListItemDto dto) {
        dto.setComments(0);
        dto.setTags(List.of("Пока без тегов"));
        return dto;
    }
}
