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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.yandex.practicum.configuration.TestWebConfig;
import ru.yandex.practicum.dto.CommentRequestDto;

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestWebConfig.class})
@WebAppConfiguration
@ActiveProfiles("test")
public class CommentControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws SQLException {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();

        ScriptUtils.executeSqlScript(dataSource.getConnection(), new ClassPathResource("test-schema.sql"));
    }

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
