package ru.yandex.practicum.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.yandex.practicum.configuration.TestWebConfig;
import ru.yandex.practicum.service.FileService;

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestWebConfig.class})
@WebAppConfiguration
@ActiveProfiles("test")
public class ImageControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private FileService fileService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() throws SQLException {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        ScriptUtils.executeSqlScript(dataSource.getConnection(), new ClassPathResource("test-schema.sql"));
    }

    @Test
    void getImageTest() throws Exception {
        reset(fileService);

        Resource mockResource = new ByteArrayResource("test mock pickture".getBytes());
        when(fileService.getImageResource("test.jpg")).thenReturn(mockResource);
        when(fileService.getImageContentType("test.jpg")).thenReturn("image/jpeg");

        mockMvc.perform(get("/images/test.jpg"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/jpeg"))
                .andExpect(content().bytes("test mock pickture".getBytes()));
    }
}