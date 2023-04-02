package com.news.portal.service;

import com.news.portal.dto.UserDto;
import com.news.portal.entity.UserEntity;
import com.news.portal.entity.Language;
import com.news.portal.service.impl.ArticleServiceImpl;
import com.news.portal.mapper.ArticleMapper;
import com.news.portal.dto.ArticleDto;
import com.news.portal.exception.ArticleAlreadyExistsException;
import com.news.portal.exception.ArticleNotFoundException;
import com.news.portal.entity.Article;
import com.news.portal.repository.ArticleRepository;
import com.news.portal.mapper.UserMapper;

import org.junit.jupiter.api.*;

import org.junit.jupiter.api.extension.ExtendWith;


import org.mockito.Mock;



import java.time.LocalDateTime;
import java.util.*;

import org.assertj.core.api.Assertions;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@TestInstance(PER_CLASS)
@ExtendWith(MockitoExtension.class)
class ArticleServiceImplTest {

    private ArticleServiceImpl articleService;
    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private ArticleMapper articleMapper;

    @Mock(lenient = true)
    private UserMapper userMapper;

    @Mock
    private LanguageService languageService;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        articleService = new ArticleServiceImpl( userMapper,articleRepository,
                articleMapper, languageService);
    }

    @Test
    @DisplayName("given Article data, when new Article is creating, then Message object is returning")
    void shouldCreateArticle_ReturnsArticleDto() {
        //given
        Language language = Language.builder()
                .id(1L)
                .code("en").build();
        UserEntity user = UserEntity.builder()
                .id(1l)
                .firstName("Aida")
                .lastName("Tynybek kyzy")
                .email("tynybekkyzy@gmail.com").build();
        ArticleDto articleDto = ArticleDto.builder()
                .id(1L)
                .title("Sample title")
                .preview("Sample preview")
                .content("Sample content")
                .publishedDate(LocalDateTime.now())
                .langCode(language.getCode())
                .build();

        /*Article article1 = Article.builder()
                .id(1L)
                .title("Sample title")
                .preview("Sample preview")
                .content("Sample content")
                .publishedDate(LocalDateTime.now())
                .build();*/
        given(languageService.getDefaultLanguage()).willReturn(language);
        given(userMapper.toDto(user)).willReturn(new UserDto(user.getId(),user.getFirstName(),user.getLastName(),user.getEmail()));

        doNothing().when(articleService).createArticle(articleDto);

    }

    @Test
    @DisplayName("given an existing Article data, when creating a new Article, then exception is thrown")
    void shouldThrownArticleAlreadyExistsException_whenSameArticleExists() {
        //given
        ArticleDto articleDto = ArticleDto.builder()
                .id(1L)
                .title("В Бишкеке разработали новые автобусные маршруты.")
                .preview("В департаменте транспорта и развития дорожно-транспортной инфраструктуры мэрии предоставили 24.kg схему.")
                .content("Маршрут № 31 — жилмассив «Ынтымак» — кольцевой, протяженность 34 километра, интервал — 7 минут. Выделено 24 автобуса.")
                .publishedDate(LocalDateTime.now())
                .build();

        String errorMsg = "Unable to save an incomplete entity : " + articleDto;
        //when
        when(articleRepository.save(any(Article.class))).thenThrow(new ArticleAlreadyExistsException(errorMsg));
        RuntimeException thrownException;
        thrownException = assertThrows(ArticleAlreadyExistsException.class,
                () -> articleService.createArticle(articleDto));

        //then
        assertEquals(errorMsg, thrownException.getMessage());

    }
    @Test
    @DisplayName("Should throw ArticleNotFoundException when article does not exist")
    void shouldThrowArticleNotFoundExceptionWhenArticleDoesNotExist() {
        // Given
        long articleId = 1L;

        // When & Then
        assertThatThrownBy(() -> articleService.getArticleByIdLocale(articleId))
                .isInstanceOf(ArticleNotFoundException.class)
                .hasMessageContaining("Article not found " + articleId);
    }

    @Test
    @DisplayName("Should return ArticleDto if the article exists")
    void shouldReturnArticleDtoWhenArticleExists() {
        // Given
        long articleId = 1L;

        // When
        Article article = Article.builder()
                .id(articleId)
                .title("Test Article")
                .preview("Test Preview")
                .content("Test Content")
                .publishedDate(LocalDateTime.now())
                .language(new Language())
                .author(new UserEntity())
                .build();
        articleRepository.save(article);

        ArticleDto expectedDto = articleMapper.toDto(article);

        // Then
        ArticleDto actualDto = articleService.getArticleByIdLocale(articleId);
        assertThat(actualDto).isEqualTo(expectedDto);
}


    @Test
    @DisplayName("given Article ID, when method get, then Article is retrieved")
    void shouldReturnArticleDto_whenFoundById() {

        //given
        ArticleDto articleDto = ArticleDto.builder()
                .id(1L)
                .title("Sample title")
                .preview("Sample preview")
                .content("Sample content")
                .publishedDate(LocalDateTime.now())
                .build();

        Article article = Article.builder()
                .id(1L)
                .title("Sample title")
                .preview("Sample preview")
                .content("Sample content")
                .publishedDate(LocalDateTime.now())
                .build();

        when(articleRepository.findById(articleDto.getId())).thenReturn(Optional.of(article));
        when(articleMapper.toDto(any())).thenReturn((articleDto));

        //when
        ArticleDto actual = articleService.getArticleByIdLocale(articleDto.getId());

        //then
        assertNotNull(article);
        assertEquals(actual.getId(), article.getId());
        verify(articleRepository).findById(article.getId());
    }


    @Test
    @DisplayName("Update Article Test")
    void shouldUpdateUser_ifFound_ReturnsArticleDto() {
        //given
        long articleId = 1L;
        Article article = Article.builder()
                .id(1L)
                .title("В Бишкеке разработали новые автобусные маршруты.")
                .preview("В департаменте \n" +
                        "                        \"транспорта и развития дорожно-транспортной инфраструктуры \n" +
                        "                        \"мэрии предоставили 24.kg схему.")
                .content("Маршрут № 31 — жилмассив «Ынтымак» — кольцевой, протяженность 34 километра, \n" +
                        "                        интервал — 7 минут. Выделено 24 автобуса.")
                .publishedDate((LocalDateTime.now()))
                .build();

        ArticleDto articleDto = ArticleDto.builder()
                .id(1L)
                .title("В Бишкеке разработали новые автобусные маршруты.")
                .preview("В департаменте \n" +
                        "                        \" транспорта и развития дорожно-транспортной инфраструктуры \n" +
                        "                        \" мэрии предоставили 24.kg схему.")
                .content("Маршрут № 31 — жилмассив «Ынтымак» — кольцевой, протяженность 34 километра, " +
                        "интервал — 7 минут. Выделено 24 автобуса.")
                .publishedDate(LocalDateTime.now())
                .build();

        //when
        when(articleRepository.findById(articleId)).thenReturn(Optional.ofNullable(article));
        assert article != null;
        when(articleRepository.save(article)).thenReturn(article);
        when(articleMapper.toDto(any())).thenReturn((articleDto));

        ArticleDto savedArticle = articleService.updateArticle(articleId, articleDto);

        Assertions.assertThat(savedArticle).isNotNull();
    }

    @Test
    @DisplayName("When given Id should delete article if found")
    public void shouldDeleteArticle_ifArticleExists() {
        //given
        long articleId = 1L;
        Article article = Article.builder()
                .id(1L)
                .title("В Бишкеке разработали новые автобусные маршруты.")
                .preview("В департаменте транспорта и развития дорожно-транспортной инфраструктуры мэрии предоставили 24.kg схему.")
                .content("Маршрут № 31 — жилмассив «Ынтымак» — кольцевой, протяженность 34 километра, интервал — 7 минут. Выделено 24 автобуса.")
                .publishedDate((LocalDateTime.now()))
                .build();

        //when
        when(articleRepository.findById(articleId)).thenReturn(Optional.ofNullable(article));

        //then
        assert article != null;
        articleService.deleteArticle(article.getId());
    }
}
