package com.news.portal.service.impl;

import com.news.portal.dto.ArticleDto;
import com.news.portal.entity.Article;
import com.news.portal.entity.Language;
import com.news.portal.entity.UserEntity;
import com.news.portal.exception.ArticleAlreadyExistsException;
import com.news.portal.exception.ArticleNotFoundException;
import com.news.portal.exception.BatchDeleteException;
import com.news.portal.exception.UserNotFoundException;
import com.news.portal.mapper.ArticleMapper;
import com.news.portal.repository.ArticleRepository;
import com.news.portal.repository.LanguageRepository;
import com.news.portal.repository.UserRepository;
import com.news.portal.service.LanguageService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestInstance(PER_CLASS)
@ExtendWith(MockitoExtension.class)
class ArticleServiceImplTest {
    @InjectMocks
    private ArticleServiceImpl articleService;
    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private ArticleMapper articleMapper;
    @Mock
    private UserRepository userRepository;

    @Mock(lenient = true)
    private LanguageRepository languageRepository;

    @Mock
    private LanguageService languageService;
    private final static long ARTICLE_ID = 1L;
    private final static String TITLE = "Test title ";
    private final static String PREVIEW = "Test preview ";
    private final static String CONTENT = "Test content ";
    private final static String LANG_EN = "en";
    private final static String LANG_LOCALE_RU = "ru";
    private final static long LANG_ID = 1L;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        articleService = new ArticleServiceImpl(articleRepository,
                articleMapper, languageService, userRepository, languageRepository);
    }

    @Test
    @DisplayName("Create new Article")
    void createArticle_ReturnsArticleDtoTest() throws UserNotFoundException, ArticleAlreadyExistsException {
        // Создаем тестовые данные
        ArticleDto articleDTO = new ArticleDto();
        articleDTO.setTitle(TITLE);
        articleDTO.setPreview(PREVIEW);
        articleDTO.setContent(CONTENT);
        articleDTO.setPublishedDate(LocalDate.now());
        articleDTO.setLangCode(LANG_EN);

        Language language = new Language();
        language.setCode(LANG_EN);

        articleDTO.setLangCode(language.getCode());

        UserEntity author = new UserEntity();
        author.setId(1L);
        author.setFirstName("John");
        author.setLastName("Doe");
        author.setEmail("johndoe@example.com");
        articleDTO.setAuthor(author);

        // Мокируем поведение репозиториев
        when(articleRepository.findByIdAndTitle(articleDTO.getId(),articleDTO.getTitle())).thenReturn(Optional.empty());
        when(languageRepository.findByCode(anyString())).thenReturn(Optional.of(language));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(author));
        when(articleRepository.save(any(Article.class))).thenReturn(new Article());


        // Вызываем тестируемый метод
        ArticleDto savedArticleDTO = articleService.createArticle(articleDTO);

        // Проверяем, что методы репозиториев были вызваны соответствующее количество раз с нужными аргументами
        verify(articleRepository, times(1)).findByIdAndTitle(articleDTO.getId(),articleDTO.getTitle());
        verify(languageRepository, times(1)).findByCode(articleDTO.getLangCode());
        verify(articleRepository, times(1)).save(any(Article.class));
        verify(userRepository, times(1)).findById(articleDTO.getAuthor().getId());


        // Проверяем, что возвращаемый объект не является null
        assertNotNull(savedArticleDTO);
        // Проверяем, что поля ArticleDTO заполнены правильными значениями
        assertEquals("Test title ", savedArticleDTO.getTitle());
        assertEquals("Test preview ", savedArticleDTO.getPreview());
        assertEquals("Test content ", savedArticleDTO.getContent());
        assertEquals(LocalDate.now(), savedArticleDTO.getPublishedDate());
        assertEquals("en", savedArticleDTO.getLangCode());
        //assertEquals(Long.valueOf(1L), savedArticleDTO.getAuthor().getId());
        assertEquals("John", savedArticleDTO.getAuthor().getFirstName());
        assertEquals("Doe", savedArticleDTO.getAuthor().getLastName());
        assertEquals("johndoe@example.com", savedArticleDTO.getAuthor().getEmail());

    }

    @Test
    @DisplayName("Create new Article throws ArticleAlreadyExistsException() if the same Article exists")
    void articleAlreadyExists_ThrowsArticleAlreadyExistsExceptionTest() {

        // Создаем тестовые данные
        ArticleDto articleDTO = new ArticleDto();
        articleDTO.setTitle(TITLE);
        articleDTO.setPreview(PREVIEW);
        articleDTO.setContent(CONTENT);
        articleDTO.setPublishedDate(LocalDate.now());
        articleDTO.setLangCode("en");

        Language language = new Language();
        language.setCode(LANG_EN);

        articleDTO.setLangCode(language.getCode());

        UserEntity author = new UserEntity();
        author.setId(1L);
        author.setFirstName("John");
        author.setLastName("Doe");
        author.setEmail("johndoe@example.com");
        articleDTO.setAuthor(author);

        when(articleRepository.findByIdAndTitle(articleDTO.getId(),articleDTO.getTitle())).thenReturn(Optional.of(new Article()));

        // Вызываем тестируемый метод и ожидаем, что будет выброшено исключение ArticleAlreadyExistsException
        assertThrows(ArticleAlreadyExistsException.class, () -> articleService.createArticle(articleDTO));

        verify(articleRepository, times(1)).findByIdAndTitle(articleDTO.getId(),articleDTO.getTitle());
        verify(languageRepository, times(0)).findByCode(articleDTO.getLangCode());
        verify(articleRepository, times(0)).save(any(Article.class));
        verify(userRepository, times(0)).findById(articleDTO.getAuthor().getId());

    }

    @Test
    @DisplayName("ArticleNotFoundException thrown when article does not exist")
    void articleNotFoundExceptionThrown_WhenArticleDoesNotExist() {
        // Given
        long articleId = 1L;

        // When & Then
        assertThatThrownBy(() -> articleService.getArticleByIdLocale(articleId))
                .isInstanceOf(ArticleNotFoundException.class)
                .hasMessageContaining("Article not found " + articleId);
    }

    @Test
    @DisplayName("Update Article Test")
    void updateUser_ifFound_ReturnsArticleDto() throws ArticleAlreadyExistsException {
        //given
        long articleId = 1L;
        Article article = Article.builder()
                .id(ARTICLE_ID)
                .title(TITLE)
                .preview(PREVIEW)
                .content(CONTENT)
                .publishedDate((LocalDate.now()))
                .build();

        ArticleDto articleDto = ArticleDto.builder()
                .id(ARTICLE_ID)
                .title(TITLE)
                .preview(PREVIEW)
                .content(CONTENT)
                .publishedDate(LocalDate.now())
                .build();

        //when
        when(articleRepository.findById(articleId)).thenReturn(Optional.ofNullable(article));
        assert article != null;
        when(articleRepository.save(article)).thenReturn(article);
        when(articleMapper.toDto(any())).thenReturn((articleDto));

        ArticleDto savedArticle = articleService.updateArticle(articleDto);

        Assertions.assertThat(savedArticle).isNotNull();
    }

    @Test
    @DisplayName("Delete Article by Id")
    public void deleteArticle_ifArticleExists() {
        //given
        long articleId = 1L;
        Article article = Article.builder()
                .id(ARTICLE_ID)
                .title(TITLE)
                .preview(PREVIEW)
                .content(CONTENT)
                .publishedDate((LocalDate.now()))
                .build();

        //when
        when(articleRepository.findById(articleId)).thenReturn(Optional.ofNullable(article));

        //then
        assert article != null;
        articleService.deleteArticle(article.getId());
    }

    @Test
    @DisplayName("Get Article by Id and Language code")
    void getArticleByIdAndLangCode() {
        Article article = Article.builder()
                .id(ARTICLE_ID)
                .title(TITLE)
                .language(new Language(LANG_EN))
                .build();

        ArticleDto expectedDto = ArticleDto.builder()
                .id(ARTICLE_ID)
                .title(TITLE)
                .build();

        when(articleRepository.findByIdAndLanguage_Code(ARTICLE_ID, LANG_EN)).thenReturn(Optional.of(article));
        when(articleMapper.toDto(article)).thenReturn(expectedDto);

        ArticleDto actualArticle = articleService.getArticleByIdAndLangCode(ARTICLE_ID, LANG_EN);

        // Assert
        assertNotNull(actualArticle);
        assertEquals(ARTICLE_ID, actualArticle.getId());
        assertEquals("Test title ", actualArticle.getTitle());


    }


    @Test
    @DisplayName("Get Article by Id Locale")
    void getArticleByIdLocaleTest() {
        Article article = Article.builder()
                .id(ARTICLE_ID)
                .title(TITLE)
                .preview(PREVIEW)
                .language(new Language(LANG_LOCALE_RU))
                .build();

        ArticleDto expectedDto = ArticleDto.builder()
                .id(ARTICLE_ID)
                .title(TITLE)
                .preview(PREVIEW)
                .build();

        when(languageService.getLanguageIdByLocale()).thenReturn(LANG_ID);
        when(articleRepository.findByIdAndLanguageId(ARTICLE_ID, LANG_ID)).thenReturn(Optional.of(article));
        when(articleMapper.toDto(article)).thenReturn(expectedDto);

        ArticleDto actualArticle = articleService.getArticleByIdLocale(ARTICLE_ID);

        // Assert
        assertNotNull(actualArticle);
        assertEquals(ARTICLE_ID, actualArticle.getId());
        assertEquals("Test title ", actualArticle.getTitle());
        assertEquals("Test preview ", actualArticle.getPreview());
    }

    @Test
    @DisplayName("Get all article LOCALE")
    void getAllArticlesLocale() {
        int pageNo = 0;
        int pageSize = 3;
        long languageId = 1L;

        Language language = new Language();
        language.setCode(LANG_LOCALE_RU);

        List<Article> articles = new ArrayList<>();
        for (int i = 1; i <= pageSize; i++) {
            Article article = new Article();
            article.setId((long) i);
            article.setTitle(TITLE + i);
            article.setPreview(PREVIEW + i + ".");
            article.setContent(CONTENT);
            article.setPublishedDate(LocalDate.now().minusDays(i));
            article.setLanguage(language);

            articles.add(article);
        }

        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.Direction.DESC, "publishedDate");
        Page<Article> articlesPage = new PageImpl<>(articles, pageable, articles.size());

        // Мокируем поведение сервиса и репозитория
        when(languageService.getLanguageIdByLocale()).thenReturn(languageId);
        when(articleRepository.findAllByLanguage_IdOrderByPublishedDate(pageable, languageId)).thenReturn(articlesPage);
        when(articleMapper.toDto(any(Article.class))).thenReturn(new ArticleDto());

        List<ArticleDto> expectedArticleDTOs = articles.stream()
                .map(articleMapper::toDto)
                .toList();

        Page<ArticleDto> actualArticleDTOPage = articleService.getAllArticlesLocale(pageNo, pageSize);

        assertNotNull(actualArticleDTOPage);
        assertEquals(expectedArticleDTOs.size(), actualArticleDTOPage.getNumberOfElements());
        List<ArticleDto> actualArticleDTOs = actualArticleDTOPage.getContent();
        assertEquals(expectedArticleDTOs.size(), actualArticleDTOs.size());

        for (int i = 0; i < expectedArticleDTOs.size(); i++) {

            ArticleDto expectedArticleDto = expectedArticleDTOs.get(i);
            ArticleDto actualArticleDto = actualArticleDTOs.get(i);

            if (expectedArticleDto == null) {
                fail("Expected article DTO at index " + i + " is null");
            }

            assertEquals(expectedArticleDto.getId(), actualArticleDto.getId());
            assertEquals(expectedArticleDto.getTitle(), actualArticleDto.getTitle());
            assertEquals(expectedArticleDto.getPreview(), actualArticleDto.getPreview());
            assertEquals(expectedArticleDto.getContent(), actualArticleDto.getContent());
            assertEquals(expectedArticleDto.getPublishedDate(), actualArticleDto.getPublishedDate());
            assertEquals(expectedArticleDto.getLangCode(), actualArticleDto.getLangCode());
        }
    }

    @Test
    @DisplayName("Get all articles by language code")
    void getAllArticlesByLangCodeTest() {
        // Create test data
        int pageNo = 0;
        int pageSize = 3;

        Language language = new Language();
        language.setCode(LANG_EN);

        List<Article> articles = new ArrayList<>();
        for (int i = 1; i <= pageSize; i++) {
            Article article = new Article();
            article.setId((long) i);
                article.setTitle(TITLE + i);
            article.setPreview(PREVIEW + i);
            article.setContent(CONTENT);
            article.setPublishedDate(LocalDate.now().minusDays(i));
            article.setLanguage(language);
            articles.add(article);
        }

        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.Direction.DESC, "publishedDate");
        Page<Article> articlesPage = new PageImpl<>(articles, pageable, articles.size());

        List<ArticleDto> expectedArticleDTOs = articles.stream()
                .map(articleMapper::toDto)
                .collect(Collectors.toList());

        // Mock repository method
        when(articleRepository.findAllByLanguage_CodeOrderByPublishedDate(pageable, LANG_EN)).thenReturn(articlesPage);

        // Call the service method
        Page<ArticleDto> actualArticleDTOPage = articleService.getAllArticlesByLangCode(pageNo, pageSize, LANG_EN);

        // Assertions
        assertNotNull(actualArticleDTOPage);
        assertEquals(expectedArticleDTOs.size(), actualArticleDTOPage.getNumberOfElements());
        List<ArticleDto> actualArticleDTOs = actualArticleDTOPage.getContent();
        assertEquals(expectedArticleDTOs.size(), actualArticleDTOs.size());
        assertEquals(expectedArticleDTOs, actualArticleDTOs);
    }

    @Test
    @DisplayName("Delete articles in batch")
    public void deleteInBatchTest() throws BatchDeleteException {
        // Create test data
        List<Long> articleIds = Arrays.asList(1L, 2L, 3L);

        Article article1 = new Article();
        article1.setId(1L);
        Article article2 = new Article();
        article2.setId(2L);
        Article article3 = new Article();
        article3.setId(3L);

        List<Article> articlesToDelete = Arrays.asList(article1, article2, article3);

        when(articleRepository.findByIdIn(articleIds)).thenReturn(articlesToDelete);
        doNothing().when(articleRepository).deleteAllInBatch(articlesToDelete);

        // Test deleteInBatch() method
        articleService.deleteInBatch(articleIds);

        // Verify that articleRepository methods were called
        verify(articleRepository, times(1)).findByIdIn(articleIds);
        verify(articleRepository, times(1)).deleteAllInBatch(articlesToDelete);
    }

    @Test
    @DisplayName("Delete articles in batch with exception")
    public void deleteInBatchWithException()  {
        // Create test data
        List<Long> articleIds = Arrays.asList(1L, 2L, 3L);

        Article article1 = new Article();
        article1.setId(1L);
        Article article2 = new Article();
        article2.setId(2L);
        Article article3 = new Article();
        article3.setId(3L);

        List<Article> articlesToDelete = Arrays.asList(article1, article2, article3);

        when(articleRepository.findByIdIn(articleIds)).thenReturn(articlesToDelete);
        doThrow(new RuntimeException("Failed to delete articles.")).when(articleRepository).deleteAllInBatch(articlesToDelete);

        // Test deleteInBatch() method and expect BatchDeleteException
        assertThrows(BatchDeleteException.class, () -> articleService.deleteInBatch(articleIds));

        // Verify that articleRepository methods were called
        verify(articleRepository, times(1)).findByIdIn(articleIds);
        verify(articleRepository, times(1)).deleteAllInBatch(articlesToDelete);
    }
}

