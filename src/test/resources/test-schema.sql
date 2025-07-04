-- Создание тестовой схемы БД
DROP TABLE IF EXISTS post_tags;
DROP TABLE IF EXISTS comment;
DROP TABLE IF EXISTS tag;
DROP TABLE IF EXISTS post;
DROP SEQUENCE IF EXISTS tag_id_seq;
DROP SEQUENCE IF EXISTS post_id_seq;
DROP SEQUENCE IF EXISTS comment_id_seq;
DROP SEQUENCE IF EXISTS post_tags_id_seq;

CREATE SEQUENCE tag_id_seq START WITH 1;
CREATE SEQUENCE post_id_seq START WITH 1;
CREATE SEQUENCE comment_id_seq START WITH 1;
CREATE SEQUENCE post_tags_id_seq START WITH 1;

CREATE TABLE post (
    id BIGINT DEFAULT nextval('post_id_seq') PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    likes INTEGER DEFAULT 0,
    filename VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

CREATE TABLE tag (
    id BIGINT DEFAULT nextval('tag_id_seq') PRIMARY KEY,
    name VARCHAR(25) NOT NULL UNIQUE
);

CREATE TABLE comment (
    id BIGINT DEFAULT nextval('comment_id_seq') PRIMARY KEY,
    post_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES post(id) ON DELETE CASCADE
);

CREATE TABLE post_tags (
    id BIGINT DEFAULT nextval('post_tags_id_seq') PRIMARY KEY,
    post_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    UNIQUE (post_id, tag_id),
    FOREIGN KEY (post_id) REFERENCES post(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tag(id) ON DELETE CASCADE
);