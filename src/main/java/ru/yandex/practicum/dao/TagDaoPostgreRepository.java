package ru.yandex.practicum.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class TagDaoPostgreRepository implements TagDao {
    private final JdbcTemplate jdbcTemplate;

    public TagDaoPostgreRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<Long, List<String>> findTagsByPostIds(List<Long> postIds) {
        if (postIds == null || postIds.isEmpty()) {
            return Collections.emptyMap();
        }
        String sql = """
                SELECT pt.post_id, t.name
                FROM post_tags pt
                JOIN tag t ON pt.tag_id = t.id
                WHERE pt.post_id IN (%s)
                ORDER BY pt.post_id, t.name
                """.formatted(postIds.stream()
                .map(id -> "?")
                .collect(Collectors.joining(",")));

        Map<Long, List<String>> result = new HashMap<>();

        jdbcTemplate.query(sql,
                (rs) -> {
                    Long postId = rs.getLong("post_id");
                    String tagName = rs.getString("name");

                    result.computeIfAbsent(postId, k -> new ArrayList<>()).add(tagName);
                },
                postIds.toArray());

        return result;
    }

    @Override
    public List<String> findTagsByPostId(Long postId) {
        String sql = """
                SELECT t.name
                FROM post_tags pt
                JOIN tag t ON pt.tag_id = t.id
                WHERE pt.post_id = ?
                ORDER BY t.name
                """;

        List<String> result = new ArrayList<>();

        jdbcTemplate.query(sql,
                (rs) -> {
                    result.add(rs.getString("name"));
                },
                postId);

        return result;
    }
}
