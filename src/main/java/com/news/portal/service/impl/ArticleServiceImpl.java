package com.news.portal.service.impl;

import com.news.portal.model.Language;
import com.news.portal.service.ArticleService;
import com.news.portal.mapper.ArticleMapper;
import com.news.portal.dto.ArticleDto;
import com.news.portal.exception.ArticleAlreadyExistsException;
import com.news.portal.exception.ArticleNotFoundException;
import com.news.portal.model.Article;
import com.news.portal.model.Message;
import com.news.portal.repository.ArticleRepository;
import com.news.portal.mapper.UserMapper;
import com.news.portal.service.LanguageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Slf4j
public class ArticleServiceImpl implements ArticleService {

    private final UserMapper userMapper;

    private final ArticleRepository articleRepository;
    private final ArticleMapper articleMapper;

    private final LanguageService languageService;

    @Autowired
    public ArticleServiceImpl(UserMapper userMapper, ArticleRepository articleRepository, ArticleMapper articleMapper, LanguageService languageService) {
        this.userMapper = userMapper;
        this.articleRepository = articleRepository;
        this.articleMapper = articleMapper;
        this.languageService = languageService;
    }

    @Override
    @Transactional
    public Message createArticle(ArticleDto articleDto) {
        log.info("Creating new article");
        Long languageId = languageService.getLanguageIdByLocale();
        Optional<Article> optionalArticle = articleRepository.findByTitle(articleDto.getTitle());

        if (optionalArticle.isPresent()) {
            log.error("The same article already exists");
            throw new ArticleAlreadyExistsException("News with title " + articleDto.getTitle() + " already exists");
        }
        Article article = new Article();
        article.setTitle(articleDto.getTitle());
        article.setPreview(articleDto.getPreview());
        article.setContent(articleDto.getContent());
        article.setAuthor(userMapper.toEntity(articleDto.getAuthor()));
        article.setPublishedDate(articleDto.getCreatedDate());
        Language language = languageService.getLanguageByLocale().orElseThrow(() -> new NoSuchElementException("Such language not found"));
        article.setLanguage(language);
        articleRepository.save(article);

        return new Message("New Article added");
    }


    @Override
    @Transactional(readOnly = true)
    public ArticleDto getArticleById(Long id) {
        long languageId = languageService.getLanguageIdByLocale();
        log.info("Getting Article by Id");
        return articleRepository.findByIdAndLanguageId(id, languageId)
                .map(articleMapper::toDto)
                .orElseThrow(
                        () -> new ArticleNotFoundException("Article not found " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ArticleDto> getAllArticles(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.Direction.DESC, "createdDate");
        Page<Article> articlesPage = articleRepository.findAllByOrderByPublishedDate(pageable);

        log.info("Getting all articles");

        return articlesPage.map(articleMapper::toDto);
    }

    @Override
    @Transactional
    public ArticleDto updateArticle(Long articleId, ArticleDto articleDto) {
        Optional<Article> optionalArticle = articleRepository.findById(articleDto.getId());

        if (optionalArticle.isEmpty()) {
            throw new ArticleNotFoundException("Article with id " + articleDto.getId() + " not found");
        }

        Article article = optionalArticle.get();
        if (articleDto.getTitle() != null && !articleDto.getTitle().equals(article.getTitle())) {
            Optional<Article> optionalExistingArticle = articleRepository.findByTitle(articleDto.getTitle());
            if (optionalExistingArticle.isPresent() && !optionalExistingArticle.get().getId().equals(article.getId())) {
                throw new ArticleAlreadyExistsException("Article with title " + articleDto.getTitle() + " already exists");
            }
            article.setTitle(articleDto.getTitle());
        }
        if (articleDto.getPreview() != null) {
            article.setPreview(articleDto.getPreview());
        }
        if (articleDto.getContent() != null) {
            article.setContent(articleDto.getContent());
        }

        if (articleDto.getAuthor() != null) {
            article.setAuthor(userMapper.toEntity(articleDto.getAuthor()));
        }

        if (articleDto.getCreatedDate() != null) {
            article.setPublishedDate(articleDto.getCreatedDate());
        }

        article = articleRepository.save(article);

        return articleMapper.toDto(article);
    }

    @Override
    @Transactional
    public Message deleteArticle(Long id) {
        Article article = articleRepository.findById(id).orElseThrow(
                () -> new ArticleNotFoundException("Article with id" + id + " not found"));
        articleRepository.delete(article);

        log.info("Deleting article with id" + article.getId());

        return new Message("Article deleted successfully");
    }


}
