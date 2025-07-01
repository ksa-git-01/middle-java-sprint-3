package ru.yandex.practicum.dto;

import java.util.List;

public class PostListItemDto {
    private Long id;
    private String title;
    private String contentPreview;
    private Integer likes;
    private String filename;
    private Integer comments;
    private List<String> tags;

    public PostListItemDto(Long id, String title, String contentPreview, Integer likes, String filename) {
        this.id = id;
        this.title = title;
        this.contentPreview = contentPreview;
        this.likes = likes;
        this.filename = filename;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentPreview() {
        return contentPreview;
    }

    public void setContentPreview(String contentPreview) {
        this.contentPreview = contentPreview;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Integer getComments() {
        return comments;
    }

    public void setComments(Integer comments) {
        this.comments = comments;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
