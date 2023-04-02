package com.news.portal.service.impl;

import com.news.portal.exception.BatchDeleteException;
import com.news.portal.entity.Language;
import com.news.portal.service.ArticleService;
import com.news.portal.mapper.ArticleMapper;
import com.news.portal.dto.ArticleDto;
import com.news.portal.exception.ArticleAlreadyExistsException;
import com.news.portal.exception.ArticleNotFoundException;
import com.news.portal.entity.Article;
import com.news.portal.entity.Message;
import com.news.portal.repository.ArticleRepository;
import com.news.portal.mapper.UserMapper;
import com.news.portal.service.LanguageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ArticleServiceImpl implements ArticleService {

    private final UserMapper userMapper;

    private final ArticleRepository articleRepository;
    private final ArticleMapper articleMapper;
    private final LanguageService languageService;


    @Autowired
    public ArticleServiceImpl(UserMapper userMapper, ArticleRepository articleRepository,
                              ArticleMapper articleMapper, LanguageService languageService) {
        this.userMapper = userMapper;
        this.articleRepository = articleRepository;
        this.articleMapper = articleMapper;
        this.languageService = languageService;
    }


    @Override
    @Transactional
    public ArticleDto createArticle(ArticleDto articleDto) {
        log.info("Creating new article");

        Optional<Article> optionalArticle = articleRepository.findByTitleAndLanguage_Code(articleDto.getTitle(), articleDto.getLangCode());

        if (optionalArticle.isPresent()) {
            log.error("The same article already exists");
            throw new ArticleAlreadyExistsException("News with title " + articleDto.getTitle() + " already exists");
        }
        Article article = new Article();
        article.setTitle(articleDto.getTitle());
        article.setPreview(articleDto.getPreview());
        article.setContent(articleDto.getContent());
        article.setAuthor(userMapper.toEntity(articleDto.getAuthor()));
        article.setPublishedDate(LocalDateTime.now());

        Language language = languageService.getLanguageByCode(articleDto.getLangCode());
        article.setLanguage(language);

        articleRepository.save(article);

        log.info("New Article saved");

        return articleMapper.toDto(article);
    }

    @Override
    public ArticleDto getArticleByIdAndLangCode(Long id, String langCode) {
        Language language = languageService.getLanguageByCode(langCode);
        log.info("Getting Article by Id and LangCode");
        return articleRepository.findByIdAndLanguage_Code(id, langCode)
                .map(articleMapper::toDto)
                .orElseThrow(
                        () -> new ArticleNotFoundException("Article not found " + id));
    }


    @Override
    @Transactional(readOnly = true)
    public ArticleDto getArticleByIdLocale(Long id) {
        long languageId = languageService.getLanguageIdByLocale();
        log.info("Getting Article by Id");
        return articleRepository.findByIdAndLanguageId(id, languageId)
                .map(articleMapper::toDto)
                .orElseThrow(
                        () -> new ArticleNotFoundException("Article not found " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ArticleDto> getAllArticlesLocale(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.Direction.DESC, "publishedDate");
        long languageId = languageService.getLanguageIdByLocale();
        Page<Article> articlesPage = articleRepository.findAllByLanguage_IdOrderByPublishedDate(pageable, languageId);

        log.info("Getting all articles by locale");

        return articlesPage.map(articleMapper::toDto);
    }

    @Override
    public Page<ArticleDto> getAllArticlesByLangCode(int pageNo, int pageSize, String langCode) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.Direction.DESC, "publishedDate");

        Page<Article> articlesPage = articleRepository.findAllByLanguage_CodeOrderByPublishedDate(pageable, langCode);

        log.info("Getting all articles by langCode");

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
            Optional<Article> optionalExistingArticle = articleRepository.findByTitleAndLanguage_Code(articleDto.getTitle(), articleDto.getLangCode());
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

        if (articleDto.getPublishedDate() != null) {
            article.setPublishedDate(articleDto.getPublishedDate());
        }

        article = articleRepository.save(article);

        return articleMapper.toDto(article);
    }

    @Override
    @Transactional
    @CacheEvict(value = "articleCache", key = "#id")
    public Message deleteArticle(Long id) {
        Article article = articleRepository.findById(id).orElseThrow(
                () -> new ArticleNotFoundException("Article with id" + id + " not found"));
        articleRepository.delete(article);

        log.info("Deleting article with id" + article.getId());

        return new Message("Article deleted successfully");
    }

    @Transactional(rollbackFor = BatchDeleteException.class)
    @CacheEvict(value = "articleCache", key = "#id")
    public void deleteInBatch(List<Long> articleIds) throws BatchDeleteException {

        List<Article> articlesToDelete = articleRepository.findByIdIn(articleIds);
        if (articlesToDelete.size() != articleIds.size()) {
            List<Long> foundIds = articlesToDelete.stream().map(Article::getId).toList();
            List<Long> notFoundIds = articleIds.stream().filter(id -> !foundIds.contains(id)).toList();
            articleIds.removeAll(foundIds);
            throw new ArticleNotFoundException("Articles with ids" + notFoundIds + " not found");
        }
        try {
            articleRepository.deleteAllInBatch(articlesToDelete);
        } catch (Exception e) {
            throw new BatchDeleteException("Failed to delete articles." + e.getMessage());
        }
    }


}
