/*create table languages
(
    id   bigserial not null,
    code varchar(2),
    primary key (id)
);

create table article
(
    id              bigserial not null,
    title           varchar(100),
    preview         varchar(500),
    content         varchar(2048),
    publishedDate   date,
    title           varchar(100),
    language_id     int8,
    author_id       int8,
    token_id        int,
    CONSTRAINT fk_author FOREIGN KEY(author_id) REFERENCES users(id)
    primary key (id)
);
*/
