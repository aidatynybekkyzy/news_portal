package com.news.portal.service;

import com.news.portal.dto.ArticleDto;
import com.news.portal.model.Message;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public interface ArticleService {
    Message addArticle(ArticleDto articleDto);

    ArticleDto getArticleById(Long id);

    Message updateArticle(Long articleId, ArticleDto articleDto);

    Message deleteArticle(Long id);

    List<ArticleDto> getAllArticlesByUserId(Long userId);

}
