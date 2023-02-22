package com.news.portal.repository;

import com.news.portal.model.Article;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class ArticleRepositoryTest {
    @Autowired
    ArticleRepository articleRepository;

    @Test
    public void shouldSaveArticle_ReturnSavedArticle() {
        //Arrange
        Article article = Article.builder()
                .title("title")
                .preview("preview")
                .content("content")
                .createdDate(LocalDateTime.now())
                .build();

        //Act
        Article savedArticle = articleRepository.save(article);

        //Assert
        Assertions.assertThat(savedArticle).isNotNull();
        Assertions.assertThat(savedArticle.getId()).isGreaterThan(0L);
    }

    @Test
    public void getAll_ShouldReturnMoreThenOneArticle() {
        Article article1 = Article.builder()
                .title("title")
                .preview("preview")
                .content("content")
                .createdDate(LocalDateTime.now())
                .build();
        Article article2 = Article.builder()
                .title("title2")
                .preview("preview2")
                .content("content2")
                .createdDate(LocalDateTime.now())
                .build();

        articleRepository.save(article1);
        articleRepository.save(article2);

        List<Article> articleList = articleRepository.findAll();

        Assertions.assertThat(articleList).isNotNull();
        Assertions.assertThat(articleList.size()).isEqualTo(2);
    }

    @Test
    public void findById_ShouldReturnArticle() {
        Article article1 = Article.builder()
                .title("title")
                .preview("preview")
                .content("content")
                .createdDate(LocalDateTime.now())
                .build();

        articleRepository.save(article1);

        Article article = articleRepository.findById(article1.getId()).get();

        Assertions.assertThat(article).isNotNull();
    }

    @Test
    public void updateArticle_ShouldReturnArticleNotNull() {
        Article article1 = Article.builder()
                .title("title")
                .preview("preview")
                .content("content")
                .createdDate(LocalDateTime.now())
                .build();

        articleRepository.save(article1);

        Article articleToSave = articleRepository.findById(article1.getId()).get();
        articleToSave.setTitle("Science");
        articleToSave.setPreview("preview preview");

        Article updatedArticle = articleRepository.save(articleToSave);

        Assertions.assertThat(updatedArticle.getTitle()).isNotNull();
        Assertions.assertThat(updatedArticle.getPreview()).isNotNull();
    }

    @Test
    public void articleDelete_ShouldReturnArticleIsEmpty() {
        Article article1 = Article.builder()
                .title("title")
                .preview("preview")
                .content("content")
                .createdDate(LocalDateTime.now())
                .build();
        articleRepository.save(article1);

        articleRepository.deleteById(article1.getId());
        Optional<Article> articleReturn = articleRepository.findById(article1.getId());

        Assertions.assertThat(articleReturn).isEmpty();
    }
}