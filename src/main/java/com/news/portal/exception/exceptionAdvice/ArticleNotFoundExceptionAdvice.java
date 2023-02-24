package com.news.portal.exception.exceptionAdvice;

import com.news.portal.exception.ArticleNotFoundException;
import com.news.portal.model.Message;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@ResponseStatus
public class ArticleNotFoundExceptionAdvice extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ArticleNotFoundException.class)
    public ResponseEntity<Message> bookNotFoundException(ArticleNotFoundException exception, WebRequest request) {
        Message message = new Message(HttpStatus.NOT_FOUND, exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
    }

}
