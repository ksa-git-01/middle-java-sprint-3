package ru.yandex.practicum.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class CommentDaoPostgreRepository implements CommentDao {
    private final JdbcTemplate jdbcTemplate;

    public CommentDaoPostgreRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<Long, Integer> findCommentCountByPostIds(List<Long> postIds) {
        if (postIds == null || postIds.isEmpty()) {
            return Collections.emptyMap();
        }
        String sql = """
                SELECT cm.post_id, count(cm.id) count
                FROM comment cm
                WHERE cm.post_id IN (%s)
                GROUP BY cm.post_id
                """.formatted(postIds.stream()
                .map(id -> "?")
                .collect(Collectors.joining(",")));

        Map<Long, Integer> result = new HashMap<>();

        jdbcTemplate.query(sql,
                (rs) -> {
                    Long postId = rs.getLong("post_id");
                    Integer count = rs.getInt("count");

                    result.put(postId, count);
                },
                postIds.toArray());

        return result;
    }
}
