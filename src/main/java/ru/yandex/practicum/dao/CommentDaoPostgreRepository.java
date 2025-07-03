package ru.yandex.practicum.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.Comment;

import java.time.LocalDateTime;
import java.util.*;
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
                SELECT post_id, count(id) count
                FROM comment
                WHERE post_id IN (%s)
                GROUP BY post_id
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

    @Override
    public List<Comment> findAllByPostId(Long postId) {
        String sql = """
                SELECT id, post_id, content, created_at, updated_at
                FROM comment
                WHERE post_id = ?
                ORDER BY created_at
                """;

        List<Comment> result = new ArrayList<>();

        jdbcTemplate.query(sql,
                (rs) -> {
                    result.add(new Comment(rs.getLong("id"),
                            rs.getLong("post_id"),
                            rs.getString("content"),
                            rs.getTimestamp("created_at").toLocalDateTime(),
                            rs.getObject("updated_at", LocalDateTime.class)));
                },
                postId);

        return result;
    }

    @Override
    public void deleteCommentByCommentId(Long commentId) {
        String sql = """
                DELETE
                FROM comment
                WHERE id = ?
                """;
        jdbcTemplate.update(sql, commentId);
    }

    @Override
    public void addCommentToPost(Long postId, String content) {
        String sql = """
                INSERT INTO comment(post_id, content)
                VALUES (?, ?)
                """;
        jdbcTemplate.update(sql, postId, content);
    }
}
