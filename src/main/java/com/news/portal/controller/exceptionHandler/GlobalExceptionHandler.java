package com.news.portal.controller.exceptionHandler;

import com.news.portal.entity.response.CustomResponse;
import com.news.portal.entity.response.MethodArgumentNotValidResponse;
import com.news.portal.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public MethodArgumentNotValidResponse handleValidationExceptions(MethodArgumentNotValidException exception) {
        return new MethodArgumentNotValidResponse(exception);
    }


    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    @ExceptionHandler({ArticleNotFoundException.class})
    public CustomResponse catchArticleNotFoundException(ArticleNotFoundException exception) {
        log.error("ArticleNotFoundException was thrown  ");
        return new CustomResponse(exception);
    }
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(PasswordIncorrectException.class)
    public CustomResponse handlePasswordIncorrectExceptions(PasswordIncorrectException exception) {
        log.error("PasswordIncorrectException was thrown due to incorrect password (ExceptionHandler logging)");
        return new CustomResponse(exception);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({AccessDeniedException.class})
    public CustomResponse accessDenied(AccessDeniedException exception) {
        log.error("AccessDeniedException was thrown. UNAUTHORIZED");
        return new CustomResponse(exception);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ArticleAlreadyExistsException.class})
    public CustomResponse articleAlreadyExist(ArticleAlreadyExistsException exception) {
        log.error("ArticleAlreadyExistsException was thrown");
        return new CustomResponse(exception);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler({BatchDeleteException.class})
    public CustomResponse handleBatchDeleteException(BatchDeleteException exception) {
        log.error(" BatchDeleteException was thrown. Error deleting articles: " + exception.getMessage());
        return new CustomResponse(exception);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({LanguageNotFoundException.class})
    public CustomResponse handleLanguageNotFoundException(LanguageNotFoundException exception) {
        log.error("LanguageNotFoundException  was thrown");
        return new CustomResponse(exception);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({UsernameNotFoundException.class})
    public CustomResponse handleUsernameNotFoundException(UsernameNotFoundException exception) {
        log.error("UsernameNotFoundException  was thrown");
        return new CustomResponse(exception);
    }


}

