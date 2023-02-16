package com.news.portal.exception;


public class ArticleAlreadyExistsException extends RuntimeException {
    public ArticleAlreadyExistsException(String the_same_article_already_exists) {
        super(the_same_article_already_exists);
    }
}
