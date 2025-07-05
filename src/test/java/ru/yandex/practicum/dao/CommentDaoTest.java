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
import ru.yandex.practicum.model.Comment;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestDataSourceConfig.class, TestDaoConfig.class})
@ActiveProfiles("test")
public class CommentDaoTest {
    @Autowired
    private DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private CommentDao commentDao;

    @BeforeEach
    void setUpSchema() throws SQLException {
        ScriptUtils.executeSqlScript(dataSource.getConnection(), new ClassPathResource("test-schema.sql"));
    }

    @Test
    void findCommentCountByPostIdsTest() {
        jdbcTemplate.update("INSERT INTO post(title, content) VALUES ('Пост 1','Содержимое поста 1')");
        jdbcTemplate.update("INSERT INTO post(title, content) VALUES ('Пост 2','Содержимое поста 2')");

        jdbcTemplate.update("INSERT INTO comment(post_id, content) VALUES (1, 'Коммент 1 для поста 1')");
        jdbcTemplate.update("INSERT INTO comment(post_id, content) VALUES (1, 'Коммент 2 для поста 1')");

        jdbcTemplate.update("INSERT INTO comment(post_id, content) VALUES (2, 'Коммент 1 для поста 2')");
        jdbcTemplate.update("INSERT INTO comment(post_id, content) VALUES (2, 'Коммент 2 для поста 2')");
        jdbcTemplate.update("INSERT INTO comment(post_id, content) VALUES (2, 'Коммент 3 для поста 2')");

        Map<Long, Integer> commentCountByPostId = commentDao.findCommentCountByPostIds(List.of(1L, 2L));

        assertEquals(2, commentCountByPostId.get(1L));
        assertEquals(3, commentCountByPostId.get(2L));
    }

    @Test
    void findAllByPostIdTest() {
        jdbcTemplate.update("INSERT INTO post(title, content) VALUES ('Пост 1','Содержимое поста 1')");

        jdbcTemplate.update("INSERT INTO comment(post_id, content) VALUES (1, 'Коммент 1 для поста 1')");
        jdbcTemplate.update("INSERT INTO comment(post_id, content) VALUES (1, 'Коммент 2 для поста 1')");

        List<Comment> testComments = commentDao.findAllByPostId(1L);

        assertEquals(2, testComments.size());
    }

    @Test
    void deleteCommentByCommentIdTest() {
        jdbcTemplate.update("INSERT INTO post(title, content) VALUES ('Пост 1','Содержимое поста 1')");

        jdbcTemplate.update("INSERT INTO comment(post_id, content) VALUES (1, 'Коммент 1 для поста 1')");
        jdbcTemplate.update("INSERT INTO comment(post_id, content) VALUES (1, 'Коммент 2 для поста 1')");

        Integer commentCountBeforeDelete = jdbcTemplate.queryForObject("SELECT count(1) FROM comment", Integer.class);

        assertEquals(2, commentCountBeforeDelete);

        commentDao.deleteCommentByCommentId(2L);

        Integer commentCountAfterDelete = jdbcTemplate.queryForObject("SELECT count(1) FROM comment", Integer.class);

        assertEquals(1, commentCountAfterDelete);
    }

    @Test
    void addCommentToPostTest() {
        jdbcTemplate.update("INSERT INTO post(title, content) VALUES ('Пост 1','Содержимое поста 1')");

        Integer commentCountBeforeCreation = jdbcTemplate.queryForObject("SELECT count(1) FROM comment", Integer.class);

        assertEquals(0, commentCountBeforeCreation);

        commentDao.addCommentToPost(1L, "Содержание тестового коммента");

        Integer commentCountAfterCreation = jdbcTemplate.queryForObject("SELECT count(1) FROM comment", Integer.class);

        assertEquals(1, commentCountAfterCreation);
    }

    @Test
    void updateCommentByIdTest() {
        jdbcTemplate.update("INSERT INTO post(title, content) VALUES ('Пост 1','Содержимое поста 1')");
        jdbcTemplate.update("INSERT INTO comment(post_id, content) VALUES (1, 'Коммент 1 для поста 1')");

        String commentContentBeforeUpdate = jdbcTemplate.queryForObject("SELECT content FROM comment WHERE id = 1", String.class);
        assertEquals("Коммент 1 для поста 1", commentContentBeforeUpdate);

        commentDao.updateCommentById(1L, "Обновленный коммент 1 для поста 1");

        String commentContentAfterUpdate = jdbcTemplate.queryForObject("SELECT content FROM comment WHERE id = 1", String.class);
        assertEquals("Обновленный коммент 1 для поста 1", commentContentAfterUpdate);
    }
}
