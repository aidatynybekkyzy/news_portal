package com.news.portal.exception;


public class ArticleAlreadyExistsException extends Exception {
    public ArticleAlreadyExistsException(String the_same_article_already_exists) {
        super(the_same_article_already_exists);
    }
}
