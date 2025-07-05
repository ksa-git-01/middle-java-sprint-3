package ru.yandex.practicum.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.yandex.practicum.configuration.TestWebConfig;
import ru.yandex.practicum.dto.CommentRequestDto;
import ru.yandex.practicum.service.FileService;

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestWebConfig.class})
@WebAppConfiguration
@ActiveProfiles("test")
public class PostControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private FileService fileService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws SQLException {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();

        ScriptUtils.executeSqlScript(dataSource.getConnection(), new ClassPathResource("test-schema.sql"));
    }

    @Test
    void getPostsTest() throws Exception {
        jdbcTemplate.update("INSERT INTO post(title, content) VALUES ('Пост 1','Содержимое поста 1')");
        jdbcTemplate.update("INSERT INTO post(title, content) VALUES ('Пост 2','Содержимое поста 2')");

        mockMvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(view().name("posts"))
                .andExpect(model().attributeExists("posts"))
                .andExpect(model().attribute("page", 0))
                .andExpect(model().attribute("size", 10));
    }

    @Test
    void showAddPostFormTest() throws Exception {
        mockMvc.perform(get("/posts/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-post"));
    }

    @Test
    void showEditPostFormTest() throws Exception {
        jdbcTemplate.update("INSERT INTO post(title, content) VALUES ('Пост 1','Содержимое поста 1')");

        mockMvc.perform(get("/posts/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-post"))
                .andExpect(model().attributeExists("post"));
    }

    @Test
    void addPostTest() throws Exception {
        reset(fileService);

        when(fileService.saveImage(any())).thenReturn("mock-filename.jpg");

        MockMultipartFile mockFile = new MockMultipartFile("image", "mock-filename.jpg", "image/jpeg", "test mock picture".getBytes());

        Integer postCountBefore = jdbcTemplate.queryForObject("SELECT count(1) FROM post", Integer.class);
        assertEquals(0, postCountBefore);

        mockMvc.perform(multipart("/posts")
                        .file(mockFile)
                        .param("title", "Новый пост")
                        .param("content", "Содержимое нового поста")
                        .param("tags", "java,spring"))
                .andExpect(status().isOk());

        Integer postCountAfter = jdbcTemplate.queryForObject("SELECT count(1) FROM post", Integer.class);
        assertEquals(1, postCountAfter);

        String filename = jdbcTemplate.queryForObject("SELECT filename FROM post WHERE id = 1", String.class);
        assertEquals("mock-filename.jpg", filename);
    }

    @Test
    void editPostTest() throws Exception {
        reset(fileService);

        when(fileService.saveImage(any())).thenReturn("mock-filename.jpg");

        jdbcTemplate.update("INSERT INTO post(title, content) VALUES ('Заголовок до обновления','Содержимое до обновления')");

        MockMultipartFile mockFile = new MockMultipartFile("image", "mock-filename.jpg", "image/jpeg", "test mock picture".getBytes());

        String titleBefore = jdbcTemplate.queryForObject("SELECT title FROM post WHERE id = 1", String.class);
        assertEquals("Заголовок до обновления", titleBefore);

        mockMvc.perform(multipart("/posts/1")
                        .file(mockFile)
                        .param("title", "Обновленный заголовок")
                        .param("content", "Обновленное содержимое")
                        .param("tags", "updated")
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk());

        String titleAfter = jdbcTemplate.queryForObject("SELECT title FROM post WHERE id = 1", String.class);
        assertEquals("Обновленный заголовок", titleAfter);

        String filename = jdbcTemplate.queryForObject("SELECT filename FROM post WHERE id = 1", String.class);
        assertEquals("mock-filename.jpg", filename);
    }

    @Test
    void getPostTest() throws Exception {
        jdbcTemplate.update("INSERT INTO post(title, content) VALUES ('Пост 1','Содержимое поста 1')");

        mockMvc.perform(get("/posts/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("post"))
                .andExpect(model().attributeExists("post"))
                .andExpect(model().attributeExists("comments"));
    }

    @Test
    void deletePostTest() throws Exception {
        jdbcTemplate.update("INSERT INTO post(title, content) VALUES ('Пост для удаления','Содержимое поста')");

        Integer postCountBefore = jdbcTemplate.queryForObject("SELECT count(1) FROM post", Integer.class);
        assertEquals(1, postCountBefore);

        mockMvc.perform(delete("/posts/1"))
                .andExpect(status().isOk());

        Integer postCountAfter = jdbcTemplate.queryForObject("SELECT count(1) FROM post", Integer.class);
        assertEquals(0, postCountAfter);
    }

    @Test
    void addCommentTest() throws Exception {
        jdbcTemplate.update("INSERT INTO post(title, content) VALUES ('Пост 1','Содержимое поста 1')");

        CommentRequestDto commentRequest = new CommentRequestDto("Новый комментарий");
        String requestBody = objectMapper.writeValueAsString(commentRequest);

        Integer commentCountBefore = jdbcTemplate.queryForObject("SELECT count(1) FROM comment", Integer.class);
        assertEquals(0, commentCountBefore);

        mockMvc.perform(post("/posts/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        Integer commentCountAfter = jdbcTemplate.queryForObject("SELECT count(1) FROM comment", Integer.class);
        assertEquals(1, commentCountAfter);
    }

    @Test
    void addLikeTest() throws Exception {
        jdbcTemplate.update("INSERT INTO post(title, content, likes) VALUES ('Пост 1','Содержимое поста 1', 10)");

        Integer likesBefore = jdbcTemplate.queryForObject("SELECT likes FROM post WHERE id = 1", Integer.class);
        assertEquals(10, likesBefore);

        mockMvc.perform(post("/posts/1/like"))
                .andExpect(status().isOk());

        Integer likesAfter = jdbcTemplate.queryForObject("SELECT likes FROM post WHERE id = 1", Integer.class);
        assertEquals(11, likesAfter);
    }
}