package com.news.portal.service;

import com.news.portal.dto.ArticleDto;
import com.news.portal.dto.ArticleResponse;
import com.news.portal.model.Message;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public interface ArticleService {
    Message createArticle(ArticleDto articleDto);

    ArticleDto getArticleById(Long id);

    ArticleDto updateArticle(Long articleId, ArticleDto articleDto);

    Message deleteArticle(Long id);

    @Transactional(readOnly = true)
    ArticleResponse getAllArticles(int pageNo, int pageSize);
}
