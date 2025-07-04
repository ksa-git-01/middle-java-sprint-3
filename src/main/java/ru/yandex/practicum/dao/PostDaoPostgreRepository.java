package ru.yandex.practicum.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.dto.CreatePostRequestDto;
import ru.yandex.practicum.dto.EditPostRequestDto;
import ru.yandex.practicum.model.Post;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class PostDaoPostgreRepository implements PostDao {
    private final JdbcTemplate jdbcTemplate;

    public PostDaoPostgreRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Post> findAllWithPagination(Integer page, Integer size) {
        Integer offset = size * page;
        return jdbcTemplate.query("""
                        SELECT id, title, content, likes, filename, created_at, updated_at
                        FROM post
                        ORDER BY id 
                        LIMIT ? OFFSET ?
                        """,
                (rs, rowNum) -> new Post(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getInt("likes"),
                        rs.getString("filename"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getObject("updated_at", LocalDateTime.class)),
                size,
                offset
        );
    }

    @Override
    public List<Post> findAllByTagWithPagination(Integer page, Integer size, String tag) {
        Integer offset = size * page;
        return jdbcTemplate.query("""
                        SELECT id, title, content, likes, filename, created_at, updated_at 
                        FROM post p
                        WHERE EXISTS (
                            SELECT 1 
                            FROM post_tags pt 
                            JOIN tag t ON pt.tag_id = t.id
                            WHERE pt.post_id = p.id 
                            AND t.name = ?
                        )
                        ORDER BY id 
                        LIMIT ? OFFSET ?
                        """,
                (rs, rowNum) -> new Post(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getInt("likes"),
                        rs.getString("filename"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getObject("updated_at", LocalDateTime.class)),
                tag,
                size,
                offset
        );
    }

    @Override
    public Post getPostByPostId(Long postId) {
        return jdbcTemplate.queryForObject("""
                        SELECT id, title, content, likes, filename, created_at, updated_at 
                        FROM post 
                        WHERE id = ?
                        """,
                (rs, rowNum) -> new Post(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getInt("likes"),
                        rs.getString("filename"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getObject("updated_at", LocalDateTime.class)),
                postId
        );
    }

    @Override
    public void deletePost(Long postId) {
        String sql = """
                DELETE
                FROM post
                WHERE id = ?
                """;
        jdbcTemplate.update(sql, postId);
    }

    @Override
    public void addLikeToPost(Long postId) {
        String sql = """
                UPDATE post
                SET likes = likes + 1 
                WHERE id = ?
                """;
        jdbcTemplate.update(sql, postId);
    }

    @Override
    public Long createPost(CreatePostRequestDto createPostRequestDto) {
        String sql = """
                INSERT INTO post(title, content, filename)
                VALUES (?, ?, ?)
                RETURNING id
                """;
        return jdbcTemplate.queryForObject(sql,
                Long.class,
                createPostRequestDto.title(),
                createPostRequestDto.content(),
                createPostRequestDto.filename());
    }

    @Override
    public void updatePost(EditPostRequestDto editPostRequestDto) {
        String sql = """
                UPDATE post
                SET title = ?,
                    content = ?,
                    filename = ?,
                    updated_at = CURRENT_TIMESTAMP
                WHERE id = ?
                """;
        jdbcTemplate.update(sql,
                editPostRequestDto.title(),
                editPostRequestDto.content(),
                editPostRequestDto.filename(),
                editPostRequestDto.id()
        );
    }
}
