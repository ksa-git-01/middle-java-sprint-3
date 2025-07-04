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
import ru.yandex.practicum.configuration.TestConfig;
import ru.yandex.practicum.configuration.TestDaoConfig;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class, TestDaoConfig.class})
@ActiveProfiles("test")
public class TagDaoTest {
    @Autowired
    private DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private TagDao tagDao;

    @BeforeEach
    void setUp() throws SQLException {
        ScriptUtils.executeSqlScript(dataSource.getConnection(), new ClassPathResource("test-schema.sql"));
    }

    @Test
    void createNewTags() {
        tagDao.createTags(List.of("tag1", "tag2"));
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM tag", Integer.class);
        assertEquals(2, count);
        List<String> newTags = jdbcTemplate.queryForList("SELECT name FROM tag ORDER BY name", String.class);
        assertEquals(List.of("tag1", "tag2"), newTags);
    }
}
