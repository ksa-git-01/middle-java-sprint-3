package ru.yandex.practicum.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.yandex.practicum.configuration.TestDaoConfig;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@Import(TestDaoConfig.class)
@Testcontainers
@Sql(scripts = "/test-schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class TagDaoTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.9-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.sql.init.mode", () -> "never");
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private TagDao tagDao;

    @Test
    void createNewTagsTest() {
        tagDao.createTags(List.of("tag1", "tag2"));

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(1) FROM tag", Integer.class);
        assertEquals(2, count);
        List<String> newTags = jdbcTemplate.queryForList("SELECT name FROM tag ORDER BY name", String.class);
        assertEquals(List.of("tag1", "tag2"), newTags);
    }

    @Test
    void createPostTagsTest() {
        jdbcTemplate.update("INSERT INTO post(title, content) VALUES ('Пост 1','Содержимое поста 1')");
        jdbcTemplate.update("INSERT INTO tag(name) VALUES ('Тег 1')");
        jdbcTemplate.update("INSERT INTO tag(name) VALUES ('Тег 2')");

        tagDao.createPostTags(1L, List.of("Тег 1", "Тег 2"));

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(1) FROM post_tags", Integer.class);
        assertEquals(2, count);
    }

    @Test
    void findTagsByPostIdTest() {
        jdbcTemplate.update("INSERT INTO post(title, content) VALUES ('Пост 1','Содержимое поста 1')");
        jdbcTemplate.update("INSERT INTO tag(name) VALUES ('Тег 1')");
        jdbcTemplate.update("INSERT INTO tag(name) VALUES ('Тег 2')");
        jdbcTemplate.update("INSERT INTO post_tags(post_id, tag_id) VALUES (1, 1)");
        jdbcTemplate.update("INSERT INTO post_tags(post_id, tag_id) VALUES (1, 2)");

        List<String> postTagsIds = tagDao.findTagsByPostId(1L);

        assertEquals(List.of("Тег 1", "Тег 2"), postTagsIds);
    }

    @Test
    void findTagsByPostIdsTest() {
        jdbcTemplate.update("INSERT INTO post(title, content) VALUES ('Пост 1','Содержимое поста 1')");
        jdbcTemplate.update("INSERT INTO post(title, content) VALUES ('Пост 2','Содержимое поста 2')");
        jdbcTemplate.update("INSERT INTO tag(name) VALUES ('Тег 1')");
        jdbcTemplate.update("INSERT INTO tag(name) VALUES ('Тег 2')");
        jdbcTemplate.update("INSERT INTO tag(name) VALUES ('Тег 3')");
        jdbcTemplate.update("INSERT INTO post_tags(post_id, tag_id) VALUES (1, 1)");
        jdbcTemplate.update("INSERT INTO post_tags(post_id, tag_id) VALUES (1, 2)");
        jdbcTemplate.update("INSERT INTO post_tags(post_id, tag_id) VALUES (2, 3)");

        Map<Long, List<String>> tagsByPostId = tagDao.findTagsByPostIds(List.of(1L, 2L));

        assertEquals(List.of("Тег 1", "Тег 2"), tagsByPostId.get(1L));
        assertEquals(List.of("Тег 3"), tagsByPostId.get(2L));
    }
}
