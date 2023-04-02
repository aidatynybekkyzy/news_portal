package com.news.portal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.news.portal.dto.ArticleDto;
import com.news.portal.entity.response.ArticleResponse;
import com.news.portal.dto.UserDto;
import com.news.portal.mapper.UserMapper;
import com.news.portal.entity.Article;
import com.news.portal.entity.Language;
import com.news.portal.entity.UserEntity;
import com.news.portal.service.ArticleService;
import com.news.portal.mapper.ArticleMapper;
import com.news.portal.service.LanguageService;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;


import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(SpringExtension.class)
class ArticleControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    ArticleService articleService;
    @Autowired
    private ObjectMapper objectMapper;
    private Article article;
    private ArticleMapper articleMapper;
    private LanguageService languageService;
    private UserMapper userMapper;

    private ArticleDto articleDto;

    @BeforeEach
    public void init() {
        article = Article.builder()
                .title("title")
                .preview("preview")
                .content("content")
                .publishedDate(LocalDateTime.now())
                .build();
        articleDto = ArticleDto.builder().title("title")
                .preview("preview")
                .content("content")
                .publishedDate(LocalDateTime.now())
                .build();
    }

    @Test
    @WithMockUser
    public void createArticle_returnsCreatedStatus() throws Exception {
        // Arrange
        Language language = Language.builder()
                .id(1L)
                .code("en").build();
        UserEntity user = UserEntity.builder()
                .id(1L)
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

        given(languageService.getDefaultLanguage()).willReturn(language); //todo check if en is default lang
        given(userMapper.toDto(user)).willReturn(new UserDto(user.getId(),user.getFirstName(),user.getLastName(), user.getEmail()));
        doNothing().when(articleService).createArticle(articleDto);

        // Act
        MockHttpServletRequestBuilder requestBuilder = post("/news_portal/article/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(articleDto))
                .with(csrf());

        // Assert
        mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(articleDto.getTitle()))
                .andExpect(jsonPath("$.preview").value(articleDto.getPreview()))
                .andExpect(jsonPath("$.content").value(articleDto.getContent()))
                .andExpect(jsonPath("$.publishedDate").value(articleDto.getPublishedDate().toString()))
                .andExpect(jsonPath("$.langCode").value(articleDto.getLangCode()))
                .andExpect(jsonPath("$.author.id").value(user.getId()))
                .andExpect(jsonPath("$.author.email").value(user.getEmail()));
    }

    @Test
    public void getAllArticle_ShouldReturnResponseArticleDto() throws Exception {
        ArticleResponse responseDto = ArticleResponse.builder().pageSize(10).last(true).pageNo(1).articleContent(Arrays.asList(articleDto)).build();
        when(articleService.getAllArticlesLocale(1, 10)).thenReturn((Page<ArticleDto>) responseDto);

        ResultActions response = mockMvc.perform(get("/news_portal/article")
                .contentType(MediaType.APPLICATION_JSON)
                .param("pageNo", "1")
                .param("pageSize", "10"));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()", CoreMatchers.is(responseDto.getArticleContent().size())));
    }

    @Test
    public void ArticleDetail_ShouldReturnArticleDto() throws Exception {
        long articleId = 1L;
        when(articleService.getArticleByIdLocale(articleId)).thenReturn(articleDto);

        ResultActions response = mockMvc.perform(get("/news_portal/article/{id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(articleDto)));

        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", CoreMatchers.is(articleDto.getTitle())))
                .andExpect(jsonPath("$.preview", CoreMatchers.is(articleDto.getPreview())))
                .andExpect(jsonPath("$.content", CoreMatchers.is(articleDto.getContent())));
    }

    @Test
    public void updateArticle_ShouldReturnArticleDto() throws Exception {
        long articleId = 1L;
        when(articleService.updateArticle(articleId, articleDto)).thenReturn(articleDto);

        ResultActions response = mockMvc.perform(put("/news_portal/article/1/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(articleDto)));

        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", CoreMatchers.is(articleDto.getTitle())))
                .andExpect(jsonPath("$.preview", CoreMatchers.is(articleDto.getPreview())))
                .andExpect(jsonPath("$.content", CoreMatchers.is(articleDto.getContent())));
    }
    @Test
    public void deleteArticle_ShouldReturnMessage() throws Exception {
        long articleId = 1L;
        doNothing().when(articleService).deleteArticle(articleId);

        ResultActions response = mockMvc.perform(delete("/news_portal/article/1/delete")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk());
    }

    @Test
    void getArticleLocale() throws Exception{
        Long articleId = 1L;

        // Mock the articleService to return a sample ArticleDto
        ArticleDto articleDto = new ArticleDto();
        articleDto.setId(articleId);
        articleDto.setTitle("Sample article title");
        articleDto.setContent("Sample article content");
        when(articleService.getArticleByIdLocale(articleId)).thenReturn(articleDto);

        // Send a GET request to /{articleId}
        mockMvc.perform(get("/" + articleId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(articleId))
                .andExpect(jsonPath("$.title").value(articleDto.getTitle()))
                .andExpect(jsonPath("$.content").value(articleDto.getContent()));

        // Verify that the articleService's getArticleByIdLocale method was called once with the correct articleId parameter
        verify(articleService, times(1)).getArticleByIdLocale(articleId);
    }

    @Test
    void getArticleByLangCode() {
    }

    @Test
    void getAllArticlesLocale() {

    }

    @Test
    void getAllArticlesByLangCode() {
    }


    @Test
    void deleteInBatchArticles() {
    }
}