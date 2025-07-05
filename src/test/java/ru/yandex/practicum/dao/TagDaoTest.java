package ru.yandex.practicum.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.yandex.practicum.configuration.TestDaoConfig;
import ru.yandex.practicum.configuration.TestDataSourceConfig;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestDataSourceConfig.class, TestDaoConfig.class})
@ActiveProfiles("test")
public class TagDaoTest {
    @Autowired
    private DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private TagDao tagDao;

    @BeforeEach
    void setUpSchema() throws SQLException {
        ScriptUtils.executeSqlScript(dataSource.getConnection(), new ClassPathResource("test-schema.sql"));
    }

    @Test
    void createNewTagsTest() {
        tagDao.createTags(List.of("tag1", "tag2"));

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM tag", Integer.class);
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

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM post_tags", Integer.class);
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
