package ru.yandex.practicum.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.service.FileService;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ImageController.class)
public class ImageControllerTest {
    @MockitoBean
    private FileService fileService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void getImageTest() throws Exception {
        reset(fileService);

        Resource mockResource = new ByteArrayResource("test mock picture".getBytes());
        when(fileService.getImageResource("test.jpg")).thenReturn(mockResource);
        when(fileService.getImageContentType("test.jpg")).thenReturn("image/jpeg");

        mockMvc.perform(get("/images/test.jpg"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/jpeg"))
                .andExpect(content().bytes("test mock picture".getBytes()));
    }
}