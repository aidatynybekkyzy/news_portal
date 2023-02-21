package com.news.portal.service;

import com.news.portal.ArticleMapper;
import com.news.portal.dto.ArticleDto;
import com.news.portal.exception.ArticleAlreadyExistsException;
import com.news.portal.exception.ArticleNotFoundException;
import com.news.portal.model.Article;
import com.news.portal.model.Message;
import com.news.portal.repository.ArticleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class ArticleServiceImpl implements ArticleService {
    private final ArticleRepository articleRepository;
    private final ModelMapper modelMapper;
    private ArticleMapper articleMapper;

    public ArticleServiceImpl(ArticleRepository articleRepository, ModelMapper modelMapper) {
        this.articleRepository = articleRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public Message addArticle(ArticleDto articleDto) {
        List<Article> articles = articleRepository.findArticleById(articleDto.getId());
        Optional<Article> existingArticle = articles.stream()
                .filter(book -> isArticleExist(articleDto).test(book))
                .findFirst();
        if (existingArticle.isPresent()) {
            throw new ArticleAlreadyExistsException("The same article already exists");
        }
        if (articleDto.getId() != null) {
            Article article = modelMapper.map(articleDto, Article.class);
            //TODO check localdatetime
            article.setCreated(LocalDateTime.now());
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
        Article article = articleRepository.findById(id).orElseThrow(
                () -> new ArticleNotFoundException("Article not found " + id));
        return articleMapper.toDto(article);
    }

    @Override
    public Message updateArticle(Long articleId, ArticleDto updatableArticleDto) {
        for (Article updatableArticle : articleRepository.findAll()) {
            if (updatableArticle.getId().equals(updatableArticleDto.getId())) {
                updatableArticle.setTitle(updatableArticleDto.getTitle());
                updatableArticle.setPreview(updatableArticleDto.getPreview());
                updatableArticle.setContent(updatableArticleDto.getContent());
                updatableArticle.setCreated(updatableArticleDto.getCreated());
                updatableArticle.setAuthor(updatableArticleDto.getAuthor());
                return new Message("Article is updated");
            }
        }
        return new Message("Article with id does not exist " + updatableArticleDto.getId());
    }

    @Override
    public Message deleteArticle(Long id) {

        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArticleDto> getAllArticlesByUserId(Long userId) {
        return articleRepository.findArticlesByAuthor_Id(userId)
                .stream()
                .map(articleMapper::toDto)
                .collect(Collectors.toList());
    }
}
