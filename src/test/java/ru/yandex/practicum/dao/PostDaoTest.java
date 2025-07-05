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
import ru.yandex.practicum.dto.CreatePostRequestDto;
import ru.yandex.practicum.dto.EditPostRequestDto;
import ru.yandex.practicum.model.Post;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestDataSourceConfig.class, TestDaoConfig.class})
@ActiveProfiles("test")
public class PostDaoTest {
    @Autowired
    private DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private PostDao postDao;

    @BeforeEach
    void setUpSchema() throws SQLException {
        ScriptUtils.executeSqlScript(dataSource.getConnection(), new ClassPathResource("test-schema.sql"));
    }

    @Test
    void findAllWithPaginationTest() {
        jdbcTemplate.update("INSERT INTO post(title, content) VALUES ('Пост 1','Содержимое поста 1')");
        jdbcTemplate.update("INSERT INTO post(title, content) VALUES ('Пост 2','Содержимое поста 2')");
        jdbcTemplate.update("INSERT INTO post(title, content) VALUES ('Пост 3','Содержимое поста 3')");

        List<Post> posts = postDao.findAllWithPagination(0, 2);

        assertEquals(2, posts.size());
    }

    @Test
    void findAllByTagWithPaginationTest() {
        jdbcTemplate.update("INSERT INTO post(title, content) VALUES ('Пост 1','Содержимое поста 1')");
        jdbcTemplate.update("INSERT INTO post(title, content) VALUES ('Пост 2','Содержимое поста 2')");

        jdbcTemplate.update("INSERT INTO tag(name) VALUES ('java')");
        jdbcTemplate.update("INSERT INTO tag(name) VALUES ('spring')");

        jdbcTemplate.update("INSERT INTO post_tags(post_id, tag_id) VALUES (1, 1)");
        jdbcTemplate.update("INSERT INTO post_tags(post_id, tag_id) VALUES (2, 1)");
        jdbcTemplate.update("INSERT INTO post_tags(post_id, tag_id) VALUES (2, 2)");

        List<Post> postsJavaTag = postDao.findAllByTagWithPagination(0, 10, "java");
        List<Post> postsSpringTag = postDao.findAllByTagWithPagination(0, 10, "spring");

        assertEquals(2, postsJavaTag.size());
        assertEquals(1, postsSpringTag.size());
    }

    @Test
    void getPostByPostIdTest() {
        jdbcTemplate.update("INSERT INTO post(title, content) VALUES ('Пост 1','Содержимое поста 1')");

        Post post = postDao.getPostByPostId(1L);

        assertEquals("Пост 1", post.title());
        assertEquals("Содержимое поста 1", post.content());
    }

    @Test
    void deletePostTest() {
        jdbcTemplate.update("INSERT INTO post(title, content) VALUES ('Пост 1','Содержимое поста 1')");
        jdbcTemplate.update("INSERT INTO post(title, content) VALUES ('Пост 2','Содержимое поста 2')");

        Integer postCountBeforeDelete = jdbcTemplate.queryForObject("SELECT count(1) FROM post", Integer.class);
        assertEquals(2, postCountBeforeDelete);

        postDao.deletePost(1L);

        Integer postCountAfterDelete = jdbcTemplate.queryForObject("SELECT count(1) FROM post", Integer.class);
        assertEquals(1, postCountAfterDelete);
    }

    @Test
    void addLikeToPostTest() {
        jdbcTemplate.update("INSERT INTO post(title, content, likes) VALUES ('Пост 1','Содержимое поста 1', 10)");

        Integer likesBeforeAdd = jdbcTemplate.queryForObject("SELECT likes FROM post WHERE id = 1", Integer.class);
        assertEquals(10, likesBeforeAdd);

        postDao.addLikeToPost(1L);

        Integer likesAfterAdd = jdbcTemplate.queryForObject("SELECT likes FROM post WHERE id = 1", Integer.class);
        assertEquals(11, likesAfterAdd);
    }

    @Test
    void createPostTest() {
        CreatePostRequestDto createPostRequestDto = new CreatePostRequestDto(
                "Пост 1",
                "Содержимое поста 1",
                "test.jpg",
                List.of("java", "spring")
        );

        Integer postCountBeforeCreate = jdbcTemplate.queryForObject("SELECT count(1) FROM post", Integer.class);
        assertEquals(0, postCountBeforeCreate);

        Long postId = postDao.createPost(createPostRequestDto);

        Integer postCountAfterCreate = jdbcTemplate.queryForObject("SELECT count(1) FROM post", Integer.class);
        assertEquals(1, postCountAfterCreate);
        assertEquals(1L, postId);
    }

    @Test
    void updatePostTest() {
        jdbcTemplate.update("INSERT INTO post(title, content, filename) VALUES ('Пост 1','Содержимое поста 1', 'old.jpg')");

        EditPostRequestDto editPostRequestDto = new EditPostRequestDto(
                1L,
                "Обновленный пост 1",
                "Обновленное содержимое поста 1",
                "new.jpg",
                List.of("tag1")
        );

        String titleBeforeUpdate = jdbcTemplate.queryForObject("SELECT title FROM post WHERE id = 1", String.class);
        assertEquals("Пост 1", titleBeforeUpdate);

        postDao.updatePost(editPostRequestDto);

        String titleAfterUpdate = jdbcTemplate.queryForObject("SELECT title FROM post WHERE id = 1", String.class);
        assertEquals("Обновленный пост 1", titleAfterUpdate);
    }
}
