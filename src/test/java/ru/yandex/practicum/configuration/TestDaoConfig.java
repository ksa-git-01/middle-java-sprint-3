package ru.yandex.practicum.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.dao.TagDao;
import ru.yandex.practicum.dao.TagDaoPostgreRepository;

@Configuration
@Profile("test")
public class TestDaoConfig {

    @Bean
    public TagDao tagDao(JdbcTemplate jdbcTemplate) {
        return new TagDaoPostgreRepository(jdbcTemplate);
    }
}