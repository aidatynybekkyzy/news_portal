package com.news.portal.exception;

public class ArticleNotFoundException extends  RuntimeException{
    public ArticleNotFoundException(String message) {
        super(message);
    }
}
