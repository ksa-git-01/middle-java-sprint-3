package ru.yandex.practicum.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import ru.yandex.practicum.controller.CommentController;
import ru.yandex.practicum.dao.CommentDao;
import ru.yandex.practicum.service.CommentService;

@Configuration
@EnableWebMvc
@Profile("test")
@Import({TestDataSourceConfig.class, TestDaoConfig.class})
public class TestWebConfig {
    @Bean
    public CommentService commentService(CommentDao commentDao) {
        return new CommentService(commentDao);
    }

    @Bean
    public CommentController commentController(CommentService commentService) {
        return new CommentController(commentService);
    }
}
