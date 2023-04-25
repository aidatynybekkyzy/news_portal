
CREATE TABLE languages
(
    id   BIGINT     NOT NULL,
    code VARCHAR(2) NOT NULL,
    PRIMARY KEY (id)
);
ALTER TABLE IF EXISTS languages
    ADD CONSTRAINT uc_languages_code UNIQUE (code);

CREATE TABLE article
(
    id             BIGINT                      NOT NULL,
    title          VARCHAR(100)                NOT NULL,
    preview        VARCHAR(500)                NOT NULL,
    content        VARCHAR(2048)               NOT NULL,
    published_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    language_id    BIGINT,
    author_id      BIGINT                      NOT NULL,
    PRIMARY KEY (id)
);

create table test_table (
    id bigserial not null
);

ALTER TABLE IF EXISTS article
    ADD CONSTRAINT FK_ARTICLE_ON_LANGUAGE FOREIGN KEY (language_id) REFERENCES languages (id);