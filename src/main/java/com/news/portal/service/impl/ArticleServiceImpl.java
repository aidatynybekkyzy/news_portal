package com.news.portal.service.impl;

import com.news.portal.service.ArticleService;
import com.news.portal.service.mapper.ArticleMapper;
import com.news.portal.dto.ArticleDto;
import com.news.portal.dto.ArticleResponse;
import com.news.portal.exception.ArticleAlreadyExistsException;
import com.news.portal.exception.ArticleNotFoundException;
import com.news.portal.model.Article;
import com.news.portal.model.Message;
import com.news.portal.repository.ArticleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final ArticleMapper articleMapper;

    @Autowired
    public ArticleServiceImpl(ArticleRepository articleRepository, ArticleMapper articleMapper) {
        this.articleRepository = articleRepository;
        this.articleMapper = articleMapper;
    }

    @Override
    @Transactional
    public Message createArticle(ArticleDto articleDto) {
        log.info("Creating new article");
        List<Article> articles = articleRepository.findArticleById(articleDto.getId());
        Optional<Article> existingArticle = articles.stream()
                .filter(book -> isArticleExist(articleDto).test(book))
                .findFirst();
        if (existingArticle.isPresent()) {
            log.error("The same article already exists");
            throw new ArticleAlreadyExistsException("The same article already exists");
        }
        if (articleDto.getId() != null) {
            Article article = articleMapper.toEntity(articleDto);
            article.setCreatedDate(LocalDateTime.now());
            articleRepository.save(article);
        }
        return new Message("New Article added");
    }

    private Predicate<Article> isArticleExist(ArticleDto articleDto) {
        return article2 -> article2.getId().equals(articleDto.getId())
                && article2.getTitle().equals(articleDto.getTitle());
    }

    @Override
    @Transactional(readOnly = true)
    public ArticleDto getArticleById(Long id) {
        log.info("Getting Article by Id");
        return articleRepository.findById(id)
                .map(articleMapper::toDto)
                .orElseThrow(
                        () -> new ArticleNotFoundException("Article not found " + id));
    }

    @Override
    @Transactional
    public ArticleDto updateArticle(Long articleId, ArticleDto updatableArticleDto) {
        Article article = articleRepository.findById(articleId).orElseThrow(()
                -> new ArticleNotFoundException("Article could not be updates"));
        article.setTitle(updatableArticleDto.getTitle());
        article.setPreview(updatableArticleDto.getPreview());
        article.setContent(updatableArticleDto.getContent());
        article.setCreatedDate(updatableArticleDto.getCreatedDate());
        article.setAuthor(updatableArticleDto.getAuthor());

        Article updatedArticle = articleRepository.save(article);
        log.info("Saving updated article");
        return articleMapper.toDto(updatedArticle); //TODO check if no mistakes
    }

    @Override
    @Transactional
    public Message deleteArticle(Long id) {
        Article article = articleRepository.findById(id).orElseThrow(
                () -> new ArticleNotFoundException("Article could not be deleted"));
        articleRepository.delete(article);
        log.info("Deleting article with id", article.getId());
        return new Message("Article deleted successfully");
    }

    @Override //TODO Pageable
    @Transactional(readOnly = true)
    public ArticleResponse getAllArticles(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Article> articles = articleRepository.findAll(pageable);
        List<Article> articleList = articles.getContent();
        List<ArticleDto> content = articleList.stream().map(articleMapper::toDto).collect(Collectors.toList());

        ArticleResponse articleResponse = new ArticleResponse();
        articleResponse.setArticleContent(content);
        articleResponse.setPageNo(articles.getNumber());
        articleResponse.setPageSize(articles.getSize());
        articleResponse.setTotalElements(articles.getTotalElements());
        articleResponse.setTotalPages(articles.getTotalPages());
        articleResponse.setLast(articles.isLast());
        log.info("Getting all articles");
        return articleResponse;
    }

}
