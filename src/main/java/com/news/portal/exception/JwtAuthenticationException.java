package com.news.portal.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

@Getter
public class JwtAuthenticationException extends AuthenticationException {

    private final HttpStatus status;

    public JwtAuthenticationException(String msg, HttpStatus status) {
        super(msg);
        this.status = status;
    }
}