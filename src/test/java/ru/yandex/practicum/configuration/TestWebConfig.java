package ru.yandex.practicum.configuration;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import ru.yandex.practicum.controller.CommentController;
import ru.yandex.practicum.controller.ImageController;
import ru.yandex.practicum.controller.PostController;
import ru.yandex.practicum.dao.CommentDao;
import ru.yandex.practicum.dao.PostDao;
import ru.yandex.practicum.dao.TagDao;
import ru.yandex.practicum.service.CommentService;
import ru.yandex.practicum.service.FileService;
import ru.yandex.practicum.service.PostService;

@Configuration
@EnableWebMvc
@Profile("test")
@Import({TestDataSourceConfig.class, TestDaoConfig.class, ThymeleafConfiguration.class})
public class TestWebConfig {
    @Bean
    public FileService fileService() {
        return Mockito.mock(FileService.class);
    }

    @Bean
    public CommentService commentService(CommentDao commentDao) {
        return new CommentService(commentDao);
    }

    @Bean
    public CommentController commentController(CommentService commentService) {
        return new CommentController(commentService);
    }

    @Bean
    public PostService postService(PostDao postDao, TagDao tagDao, CommentDao commentDao) {
        return new PostService(postDao, tagDao, commentDao);
    }

    @Bean
    public PostController postController(PostService postService,
                                         CommentService commentService,
                                         FileService fileService) {
        return new PostController(postService, commentService, fileService);
    }

    @Bean
    public ImageController imageController(FileService fileService) {
        return new ImageController(fileService);
    }
}
