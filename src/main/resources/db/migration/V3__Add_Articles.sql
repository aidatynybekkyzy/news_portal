insert into article (id,author_id, content, language_id, preview, published_date, title)
values (nextval('article_sequence'),1, 'content 1', 1, 'preview 1', '2023-03-31 22:44:53.637785','title 1'),
(nextval('article_sequence'),1, 'content 2', 1, 'preview 2', '2023-03-31 22:44:53.637785','title 2'),
(nextval('article_sequence'), 1, 'content 3', 1, 'preview 3', '2023-03-31 22:44:53.637785','title 3');
