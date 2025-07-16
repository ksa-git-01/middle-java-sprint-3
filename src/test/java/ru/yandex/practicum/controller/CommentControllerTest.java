package ru.yandex.practicum.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.yandex.practicum.dto.CommentRequestDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@Sql(scripts = "/test-schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@AutoConfigureMockMvc
public class CommentControllerTest {
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
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deleteCommentTest() throws Exception {
        jdbcTemplate.update("INSERT INTO post(title, content) VALUES ('Пост 1','Содержимое поста 1')");
        jdbcTemplate.update("INSERT INTO comment(post_id, content) VALUES (1, 'Тестовый комментарий')");

        Integer commentCountBefore = jdbcTemplate.queryForObject("SELECT count(1) FROM comment", Integer.class);
        assertEquals(1, commentCountBefore);

        mockMvc.perform(delete("/comments/1"))
                .andExpect(status().isOk());

        Integer commentCountAfter = jdbcTemplate.queryForObject("SELECT count(1) FROM comment", Integer.class);
        assertEquals(0, commentCountAfter);
    }

    @Test
    void updateCommentTest() throws Exception {
        jdbcTemplate.update("INSERT INTO post(title, content) VALUES ('Пост 1','Содержимое поста 1')");
        jdbcTemplate.update("INSERT INTO comment(post_id, content) VALUES (1, 'Исходный комментарий')");

        String contentBefore = jdbcTemplate.queryForObject("SELECT content FROM comment WHERE id = 1", String.class);
        assertEquals("Исходный комментарий", contentBefore);

        CommentRequestDto requestDto = new CommentRequestDto("Обновленный комментарий");
        String requestBody = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(put("/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        String contentAfter = jdbcTemplate.queryForObject("SELECT content FROM comment WHERE id = 1", String.class);
        assertEquals("Обновленный комментарий", contentAfter);
    }
}
