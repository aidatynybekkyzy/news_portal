package com.news.portal.service;

import com.news.portal.service.impl.ArticleServiceImpl;
import com.news.portal.mapper.ArticleMapper;
import com.news.portal.dto.ArticleDto;
import com.news.portal.exception.ArticleAlreadyExistsException;
import com.news.portal.exception.ArticleNotFoundException;
import com.news.portal.model.Article;
import com.news.portal.model.Message;
import com.news.portal.repository.ArticleRepository;
import com.news.portal.mapper.UserMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.time.LocalDateTime;
import java.util.*;

import org.assertj.core.api.Assertions;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestInstance(PER_CLASS)
@ExtendWith(MockitoExtension.class)
class ArticleServiceImplTest {

    private ArticleServiceImpl articleService;
    @Mock
    private ArticleRepository articleRepositoryMock;
    @Mock
    private ArticleMapper articleMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private LanguageService languageService;

    Message expectedMessage = new Message("New Article added");

    @BeforeEach
    public void setUp() {
        articleService = new ArticleServiceImpl(userMapper, articleRepositoryMock, articleMapper, languageService);
    }

    @Test
    @DisplayName("given Article data, when new Article is creating, then Message object is returning")
    void shouldCreateArticle_ReturnsArticleDto() {
        //given
        ArticleDto articleDto = ArticleDto.builder()
                .id(1L)
                .title("В Бишкеке разработали новые автобусные маршруты.")
                .preview("В департаменте \n" +
                        "                        \" транспорта и развития дорожно-транспортной инфраструктуры \n" +
                        "                        \" мэрии предоставили 24.kg схему.")
                .content("Маршрут № 31 — жилмассив «Ынтымак» — кольцевой, протяженность 34 километра, " +
                        "интервал — 7 минут. Выделено 24 автобуса.")
                .createdDate(LocalDateTime.now())
                .build();

        Article article1 = Article.builder()
                .id(1L)
                .title("В Бишкеке разработали новые автобусные маршруты.")
                .preview("В департаменте \n" +
                        "                        \"транспорта и развития дорожно-транспортной инфраструктуры \n" +
                        "                        \"мэрии предоставили 24.kg схему.")
                .content("Маршрут № 31 — жилмассив «Ынтымак» — кольцевой, протяженность 34 километра, \n" +
                        "                        интервал — 7 минут. Выделено 24 автобуса.")
                .createdDate((LocalDateTime.now()))
                .build();

        when(articleRepositoryMock.save(any(Article.class))).thenReturn(article1);
        Message actualMessage = articleService.createArticle(articleDto);

        assertNotNull(article1.getId());
        assertEquals(expectedMessage.getMessage(), actualMessage.getMessage());
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
                .createdDate(LocalDateTime.now())
                .build();

        String errorMsg = "Unable to save an incomplete entity : " + articleDto;
        //when
        when(articleRepositoryMock.save(any(Article.class))).thenThrow(new ArticleAlreadyExistsException(errorMsg));
        RuntimeException thrownException;
        thrownException = assertThrows(ArticleAlreadyExistsException.class,
                () -> articleService.createArticle(articleDto));

        //then
        assertEquals(errorMsg, thrownException.getMessage());

    }


    @Test
    @DisplayName("given Article ID, when method get, then Article is retrieved")
    void shouldReturnArticleDto_whenFoundById() {

        //given
        long existingArticleId = 1L;
        Article article = new Article();
        article.setId(1L);

        ArticleDto articleDto = new ArticleDto();
        articleDto.setId(1L);

        when(articleRepositoryMock.findById(existingArticleId)).thenReturn(Optional.of(article));
        when(articleMapper.toDto(any())).thenReturn((articleDto));

        //when
        ArticleDto actual = articleService.getArticleById(existingArticleId);

        //then
        assertNotNull(article);
        assertEquals(actual.getId(), article.getId());
        verify(articleRepositoryMock).findById(article.getId());
    }

    @Test
    @DisplayName("given Article ID, when method get non existing article, then Exception is thrown")
    void shouldThrownException_WhenGivenArticleDoesNotExists() {
        //given
        long nonExistingArticleId = 404L;
        String errorMsg = "Article not found " + nonExistingArticleId;

        //when
        ArticleNotFoundException thrownException = assertThrows(ArticleNotFoundException.class,
                () -> articleService.getArticleById(nonExistingArticleId));

        //then
        assertEquals(errorMsg, thrownException.getMessage());
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
                .createdDate((LocalDateTime.now()))
                .build();

        ArticleDto articleDto = ArticleDto.builder()
                .id(1L)
                .title("В Бишкеке разработали новые автобусные маршруты.")
                .preview("В департаменте \n" +
                        "                        \" транспорта и развития дорожно-транспортной инфраструктуры \n" +
                        "                        \" мэрии предоставили 24.kg схему.")
                .content("Маршрут № 31 — жилмассив «Ынтымак» — кольцевой, протяженность 34 километра, " +
                        "интервал — 7 минут. Выделено 24 автобуса.")
                .createdDate(LocalDateTime.now())
                .build();

        //when
        when(articleRepositoryMock.findById(articleId)).thenReturn(Optional.ofNullable(article));
        assert article != null;
        when(articleRepositoryMock.save(article)).thenReturn(article);
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
                .createdDate((LocalDateTime.now()))
                .build();

        //when
        when(articleRepositoryMock.findById(articleId)).thenReturn(Optional.ofNullable(article));

        //then
        assert article != null;
        articleService.deleteArticle(article.getId());
    }

   /*  @Test
     void shouldReturnAllArticles_ReturnsArticleResponse() {
         //given
         Page<Article> articles = Mockito.mock(Page.class);

         //when
         when(articleRepositoryMock.findAll(any(Pageable.class))).thenReturn(articles);

         Page<ArticleDto> savedArticle = articleService.getAllArticles(1,10);

         //then
         Assertions.assertThat(savedArticle).isNotNull();
     }
    @Test
    public void testGetAllNews() {
        LocalDateTime date1 = LocalDateTime.of(2022, 2, 15, 10, 0, 0);
        LocalDateTime date2 = LocalDateTime.of(2022, 2, 16, 10, 0, 0);
        LocalDateTime date3 = LocalDateTime.of(2022, 2, 17, 10, 0, 0);

        UserEntity author1 = new UserEntity(1L,"FirstName1", "LastName1", "email1@gmail.com1","password1");
        UserEntity author2 = new UserEntity(2L,"FirstName2", "LastName2", "email2@gmail.com2","password1");
        UserEntity author3 = new UserEntity(3L,"FirstName3", "LastName3", "email3@gmail.com3","password3");

        Article article1 = new Article(1L, "Title 1","Preview 1", "Content 1", date1, author1);
        Article article2 = new Article(2L, "Title 2","Preview 2", "Content 2", date2, author2);
        Article article3 = new Article(3L, "Title 3","Preview 3", "Content 3", date3,author3);

        Pageable pageable = PageRequest.of(0, 3, Sort.by("dateCreated").descending());
        Page<Article> newsPage = new PageImpl<>(Arrays.asList(article3, article2, article1), pageable, 3);


        when(articleRepositoryMock.findAll(pageable)).thenReturn(newsPage);

        Page<ArticleDto> articles = articleService.getAllArticles(0, 10);

        assertEquals(3, articles.getSize());
        assertEquals(3L,articles.getContent().get(2).getId() );
        assertEquals("Title 3", articles.getContent().get(2).getTitle());
        assertEquals("Preview 3", articles.getContent().get(2).getPreview());
        assertEquals("Content 3",articles.getContent().get(2).getContent());
        assertEquals(date3, articles.getContent().get(2).getCreatedDate());
       // assertEquals(author3, articles.getContent().get(2).getAuthor());

        assertEquals(2L,articles.getContent().get(1).getId() );
        assertEquals("Title 2", articles.getContent().get(1).getTitle());
        assertEquals("Preview 2", articles.getContent().get(1).getPreview());
        assertEquals("Content 2",articles.getContent().get(1).getContent());
        assertEquals(date2, articles.getContent().get(1).getCreatedDate());
     //   assertEquals(author2, articles.getContent().get(1).getAuthor());

        assertEquals(1L,articles.getContent().get(0).getId() );
        assertEquals("Title 1", articles.getContent().get(0).getTitle());
        assertEquals("Preview 1", articles.getContent().get(0).getPreview());
        assertEquals("Content 1",articles.getContent().get(0).getContent());
        assertEquals(date1, articles.getContent().get(0).getCreatedDate());
       // assertEquals(author1, articles.getContent().get(0).getAuthor());
    }*/
}
