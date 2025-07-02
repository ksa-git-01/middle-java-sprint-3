package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.dao.CommentDao;
import ru.yandex.practicum.dao.PostDao;
import ru.yandex.practicum.dao.TagDao;
import ru.yandex.practicum.dto.PostListItemDto;
import ru.yandex.practicum.model.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final PostDao postDao;
    private final TagDao tagDao;
    private final CommentDao commentDao;

    public PostService(PostDao postDao, TagDao tagDao, CommentDao commentDao) {
        this.postDao = postDao;
        this.tagDao = tagDao;
        this.commentDao = commentDao;
    }

    public List<PostListItemDto> getPostListItems(Integer page, Integer size) {
        List<PostListItemDto> postListItems = new ArrayList<>();
        List<Post> posts = postDao.findAllWithPagination(page, size);
        if (posts.isEmpty()) {
            return postListItems;
        }

        List<Long> postIds = posts.stream()
                .map(Post::id)
                .toList();

        Map<Long, List<String>> tagsByPostId = tagDao.findTagsByPostIds(postIds);
        Map<Long, Integer> commentCountByPostId = commentDao.findCommentCountByPostIds(postIds);

        return posts.stream()
                .map(post -> mapToPostListItemDto(post, tagsByPostId, commentCountByPostId))
                .toList();
    }

    private PostListItemDto mapToPostListItemDto(Post post,
                                                 Map<Long, List<String>> tagsByPostId,
                                                 Map<Long, Integer> commentCountByPostId) {
        PostListItemDto item = new PostListItemDto(post.id(),
                post.title(),
                truncateContent(post.content()),
                post.likes(),
                post.filename()
        );

        item.setTags(tagsByPostId.getOrDefault(post.id(), List.of("Нет тегов")));
        item.setComments(commentCountByPostId.getOrDefault(post.id(), 0));

        return item;
    }

    private String truncateContent(String content) {
        if (content == null) return "";
        return content.lines()
                .limit(3)
                .collect(Collectors.joining("\n"));
    }
}
