package com.news.portal.exception;

public class LanguageNotFoundException extends RuntimeException {
    public LanguageNotFoundException(String langCode) {
        super("Language not found with id: " + langCode);
    }
}
