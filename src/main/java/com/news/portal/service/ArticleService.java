package com.news.portal.service;

import com.news.portal.dto.ArticleDto;
import com.news.portal.model.Message;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public interface ArticleService {
    Message createArticle(ArticleDto articleDto);

    ArticleDto getArticleById(Long id);

    ArticleDto updateArticle(Long articleId, ArticleDto articleDto);

    void deleteArticle(Long id);

    //TODO Pageable
    @Transactional(readOnly = true)
    List<ArticleDto> getAllArticlesByUserId(Long userId);
}
