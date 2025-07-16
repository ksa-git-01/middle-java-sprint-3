package ru.yandex.practicum.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.dao.*;

@TestConfiguration
public class TestDaoConfig {

    @Bean
    public TagDao tagDao(JdbcTemplate jdbcTemplate) {
        return new TagDaoPostgreRepository(jdbcTemplate);
    }

    @Bean
    public CommentDao commentDao(JdbcTemplate jdbcTemplate) {
        return new CommentDaoPostgreRepository(jdbcTemplate);
    }

    @Bean
    public PostDao postDao(JdbcTemplate jdbcTemplate) {
        return new PostDaoPostgreRepository(jdbcTemplate);
    }
}