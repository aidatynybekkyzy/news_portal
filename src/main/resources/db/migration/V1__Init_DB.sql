DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS languages;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS article;
DROP TABLE IF EXISTS hibernate_sequence;
DROP TABLE IF EXISTS token;



CREATE SEQUENCE IF NOT EXISTS hibernate_sequence START WITH 1 INCREMENT BY 1;
create sequence users_sequence start 1 increment 1;
create sequence roles_sequence start 1 increment 1;
create sequence article_sequence start 1 increment 1;
create sequence language_sequence start 1 increment 1;

CREATE TABLE languages
(
    id   BIGINT     NOT NULL,
    code VARCHAR(2) NOT NULL,
    CONSTRAINT pk_languages PRIMARY KEY (id)
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
    CONSTRAINT pk_article PRIMARY KEY (id)
);

CREATE TABLE roles
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    role_name VARCHAR(255),
    CONSTRAINT pk_roles PRIMARY KEY (id)
);

CREATE TABLE token
(
    id         BIGINT  NOT NULL,
    token      VARCHAR(255),
    token_type VARCHAR(255),
    revoked    BOOLEAN NOT NULL,
    expired    BOOLEAN NOT NULL,
    user_id    BIGINT,
    CONSTRAINT pk_token PRIMARY KEY (id)
);

CREATE TABLE user_roles
(
    role_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL
);

CREATE TABLE users
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    firstname VARCHAR(50)                             NOT NULL,
    lastname  VARCHAR(50)                             NOT NULL,
    email     VARCHAR(255),
    password  VARCHAR(60)                             NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);
ALTER TABLE IF EXISTS article
    ADD CONSTRAINT FK_ARTICLE_ON_AUTHOR FOREIGN KEY (author_id) REFERENCES users (id);

ALTER TABLE IF EXISTS article
    ADD CONSTRAINT FK_ARTICLE_ON_LANGUAGE FOREIGN KEY (language_id) REFERENCES languages (id);

ALTER TABLE IF EXISTS token
    ADD CONSTRAINT uc_token_token UNIQUE (token);

ALTER TABLE IF EXISTS token
    ADD CONSTRAINT FK_TOKEN_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE IF EXISTS user_roles
    ADD CONSTRAINT fk_userol_on_role FOREIGN KEY (role_id) REFERENCES roles (id);

ALTER TABLE IF EXISTS user_roles
    ADD CONSTRAINT fk_userol_on_user_entity FOREIGN KEY (user_id) REFERENCES users (id);

