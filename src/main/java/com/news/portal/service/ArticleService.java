package com.news.portal.service;

import com.news.portal.dto.ArticleDto;
import com.news.portal.exception.BatchDeleteException;
import com.news.portal.entity.Message;

import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ArticleService {
    ArticleDto createArticle(ArticleDto articleDto);

    ArticleDto getArticleByIdAndLangCode(Long id, String langCode);

    ArticleDto getArticleByIdLocale(Long id);

    ArticleDto updateArticle(Long articleId, ArticleDto articleDto);

    Message deleteArticle(Long id);

    @Transactional(readOnly = true)
    Page<ArticleDto> getAllArticlesLocale(int pageNo, int pageSize);

    @Transactional(readOnly = true)
    Page<ArticleDto> getAllArticlesByLangCode(int pageNo, int pageSize, String langCode);

    void deleteInBatch(List<Long> articleIds) throws BatchDeleteException;
}
