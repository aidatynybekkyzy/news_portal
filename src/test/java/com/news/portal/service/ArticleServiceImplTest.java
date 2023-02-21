package com.news.portal.service;

import com.news.portal.dto.ArticleDto;
import com.news.portal.exception.ArticleAlreadyExistsException;
import com.news.portal.exception.ArticleNotFoundException;
import com.news.portal.model.Article;
import com.news.portal.model.Message;
import com.news.portal.repository.ArticleRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;


import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestInstance(PER_CLASS)
@ExtendWith(MockitoExtension.class)
class ArticleServiceImplTest {

    private ArticleService underTest;
    private ArticleServiceImpl articleService;
    @Mock
    private ArticleRepository articleRepositoryMock;
    private AutoCloseable autoCloseable;
    private ArticleNotFoundException articleNotFoundException;

    Message expectedMessage = new Message("New Article added");

    @BeforeEach
    public void setUp() {
        articleService = new ArticleServiceImpl(articleRepositoryMock, new ModelMapper());
    }
    ArticleDto articleDto = ArticleDto.builder()
            .id(1l)
            .title("В Бишкеке разработали новые автобусные маршруты.")
            .preview("В департаменте \n" +
                    "                        \" транспорта и развития дорожно-транспортной инфраструктуры \n" +
                    "                        \" мэрии предоставили 24.kg схему.")
            .content("Маршрут № 31 — жилмассив «Ынтымак» — кольцевой, протяженность 34 километра, " +
                    "интервал — 7 минут. Выделено 24 автобуса.")
            .created(LocalDateTime.now())
            .build();

    Article article1 = Article.builder()
            .id(1L)
            .title("В Бишкеке разработали новые автобусные маршруты.")
            .preview("В департаменте \n" +
                    "                        \"транспорта и развития дорожно-транспортной инфраструктуры \n" +
                    "                        \"мэрии предоставили 24.kg схему.")
            .content("Маршрут № 31 — жилмассив «Ынтымак» — кольцевой, протяженность 34 километра, \n" +
                    "                        интервал — 7 минут. Выделено 24 автобуса.")
            .created((LocalDateTime.now()))
            .build();

    @Test
    @DisplayName("given Article data, when new Article is creating, then Message object is returning")
    void CanAddArticle() {
        //given


        //TODO finish
        when(articleRepositoryMock.save(any(Article.class))).thenReturn(article1);
        Message actualMessage = articleService.addArticle(articleDto);

        assertNotNull(article1.getId());
        assertEquals(expectedMessage.getMessage(), actualMessage.getMessage());
    }

    @Test
    @Disabled
    @DisplayName("given an existing Article data, when creating a new Article, then exception is thrown")
    void givenAnExistingArticleData_WhenCreatingNewArticle_ThenArticleAlreadyExistsExceptionThrown() {
        //given
        ArticleDto articleDto = ArticleDto.builder()
                .id(1l)
                .title("В Бишкеке разработали новые автобусные маршруты.")
                .preview("В департаменте транспорта и развития дорожно-транспортной инфраструктуры мэрии предоставили 24.kg схему.")
                .content("Маршрут № 31 — жилмассив «Ынтымак» — кольцевой, протяженность 34 километра, интервал — 7 минут. Выделено 24 автобуса.")
                .created(LocalDateTime.now())
                .build();

        Article article1 = Article.builder()
                .id(1L)
                .title("В Бишкеке разработали новые автобусные маршруты.")
                .preview("В департаменте транспорта и развития дорожно-транспортной инфраструктуры мэрии предоставили 24.kg схему.")
                .content("Маршрут № 31 — жилмассив «Ынтымак» — кольцевой, протяженность 34 километра, интервал — 7 минут. Выделено 24 автобуса.")
                .created((LocalDateTime.now()))
                .build();
        String errorMsg = "Unable to save an incomplete entity : " + articleDto;
        //when
        when(articleRepositoryMock.save(article1)).thenThrow(new ArticleAlreadyExistsException(errorMsg));
        ArticleAlreadyExistsException thrownException = assertThrows(ArticleAlreadyExistsException.class,
                () -> articleService.addArticle(articleDto));

        //then
        assertEquals(errorMsg, thrownException);

    }


    @Test
    @DisplayName("given Article ID, when method get, then Article is retrieved")
    void givenArticleId_WhenGetMethod_ThenArticleRetrieved() {

        //given
        long existingArticleId = 1l;
        Article article = Article.builder()
                .id(1l)
                .title("В Бишкеке разработали новые автобусные маршруты.")
                .preview("В департаменте транспорта и развития дорожно-транспортной инфраструктуры мэрии предоставили 24.kg схему.")
                .content("Маршрут № 31 — жилмассив «Ынтымак» — кольцевой, протяженность 34 километра, интервал — 7 минут. Выделено 24 автобуса.")
                .created(LocalDateTime.now())
                .build();

        when(articleRepositoryMock.findArticleById(existingArticleId)).thenReturn(Collections.singletonList(article)    );

        //when
        ArticleDto articleDto = articleService.getArticleById(existingArticleId);

        //then
        assertNotNull(article);
        assertNotNull(article.getId());
        assertEquals(articleDto.getId(), article.getId());

    }

    @Test
    @DisplayName("given Article ID, when method get non existing article, then Exception is thrown")
    void givenArticleId_WhenGetMethod_ThenExceptionThrown() {
        //given
        long nonExistingArticleId = 404L;
        String errorMsg = "Article not found " + nonExistingArticleId;

        //when
        ArticleNotFoundException thrownException = assertThrows(ArticleNotFoundException.class,
                ()-> articleService.getArticleById(nonExistingArticleId));

        //then
        assertEquals(errorMsg, thrownException.getMessage());
    }

    @Test
    @DisplayName("Update Article Test")
    void whenGivenId_shouldUpdateUser_ifFound() {

    }
}
