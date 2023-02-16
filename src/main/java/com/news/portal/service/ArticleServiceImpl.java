package com.news.portal.service;

import com.news.portal.ArticleMapper;
import com.news.portal.dto.ArticleDto;
import com.news.portal.exception.ArticleAlreadyExistsException;
import com.news.portal.model.Article;
import com.news.portal.model.Message;
import com.news.portal.model.User;
import com.news.portal.repository.ArticleRepository;
import com.news.portal.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Predicate;

public class ArticleServiceImpl implements ArticleService {
    ArticleRepository articleRepository;
    UserRepository userRepository;
    ArticleMapper articleMapper;

    @Override
    public Message addArticle(ArticleDto articleDto, Long authorId) {
        List<Article> articles = articleRepository.findArticleByAuthorId(authorId);
        Optional<Article> existingArticle = articles.stream()
                .filter(article -> isArticleExist(articleDto).test(article))
                .findFirst();
        if (existingArticle.isPresent()) {
            throw new ArticleAlreadyExistsException("The same article already exists");
        }
        User user = userRepository.findById(authorId).orElseThrow(NoSuchElementException::new);
        Article article = Optional.of(articleDto)
                .map(articleMapper::toEntity).orElseThrow(NoSuchElementException::new);
        article.setAuthor(user);

        articleRepository.save(article);
        return new Message("New Article added");
    }

    private Predicate<Article> isArticleExist(ArticleDto articleDto) {
        return article2 ->article2.getId().equals(articleDto.getId())
                && article2.getTitle().equals(articleDto.getTitle());
    }

    @Override
    public ArticleDto getArticleById(Long id) {
        return null;
    }

    @Override
    public Message updateArticle(Long articleId, ArticleDto articleDto) {
        return null;
    }

    @Override
    public Message deleteArticle(Long id) {
        return null;
    }

    @Override
    public List<ArticleDto> getAllArticlesByUserId(Long userId) {
        return null;
    }
}
