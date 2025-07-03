package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.dao.CommentDao;
import ru.yandex.practicum.dao.PostDao;
import ru.yandex.practicum.dao.TagDao;
import ru.yandex.practicum.dto.PostItemDto;
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

    public List<PostListItemDto> getPostListItems(Integer page, Integer size, String tag) {
        List<PostListItemDto> postListItems = new ArrayList<>();
        List<Post> posts = findPosts(page, size, tag);
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

    public PostItemDto getPostItem(Long postId) {
        Post post = postDao.getPostByPostId(postId);
        List<String> tags = tagDao.findTagsByPostId(postId);
        return mapToPostItemDto(post, tags);
    }

    private List<Post> findPosts(Integer page, Integer size, String tag) {
        if (tag != null && !tag.trim().isEmpty()) {
            return postDao.findAllByTagWithPagination(page, size, tag.trim());
        } else {
            return postDao.findAllWithPagination(page, size);
        }
    }

    private PostListItemDto mapToPostListItemDto(Post post,
                                                 Map<Long, List<String>> tagsByPostId,
                                                 Map<Long, Integer> commentCountByPostId) {
        return new PostListItemDto(post.id(),
                post.title(),
                truncateContent(post.content()),
                post.likes(),
                post.filename(),
                commentCountByPostId.getOrDefault(post.id(), 0),
                tagsByPostId.getOrDefault(post.id(), List.of("Нет тегов")));
    }

    private PostItemDto mapToPostItemDto(Post post, List<String> tags) {
        return new PostItemDto(post.id(),
                post.title(),
                post.content(),
                post.likes(),
                post.filename(),
                tags
        );
    }

    private String truncateContent(String content) {
        if (content == null) return "";
        return content.lines()
                .limit(3)
                .collect(Collectors.joining("\n"));
    }

    public void addCommentToPost(Long postId, String content) {
        commentDao.addCommentToPost(postId, content);
    }

    public void deletePost(Long postId) {
        postDao.deletePost(postId);
    }
}
