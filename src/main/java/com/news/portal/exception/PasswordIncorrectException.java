package com.news.portal.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PasswordIncorrectException extends Throwable {
    public PasswordIncorrectException(String message) {
        super(message);
        log.error ("PasswordIncorrectException was thrown (Exception logging)");
    }
}