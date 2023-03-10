package com.news.portal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.news.portal.dto.ArticleDto;
import com.news.portal.dto.ArticleResponse;
import com.news.portal.model.Article;
import com.news.portal.service.ArticleService;
import com.news.portal.service.mapper.ArticleMapper;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.Arrays;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(controllers = ArticleController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class ArticleControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    ArticleService articleService;
    @Autowired
    private ObjectMapper objectMapper;
    private Article article;
    private ArticleMapper articleMapper;
    private ArticleDto articleDto;

    @BeforeEach
    public void init() {
        article = Article.builder()
                .title("title")
                .preview("preview")
                .content("content")
                .createdDate(LocalDateTime.now())
                .build();
        articleDto = ArticleDto.builder().title("title")
                .preview("preview")
                .content("content")
                .createdDate(LocalDateTime.now())
                .build();
    }

    @Test
    public void createPokemon_ShouldReturnCreated() throws Exception {
        given(articleService.createArticle(ArgumentMatchers.any())).willAnswer((invocation ->
                invocation.getArgument(0)));

        ResultActions response = mockMvc.perform(post("/news_portal/article/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(articleDto)));

        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", CoreMatchers.is(articleDto.getTitle())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.preview", CoreMatchers.is(articleDto.getPreview())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", CoreMatchers.is(articleDto.getContent())));
    }

    @Test
    public void getAllArticle_ShouldReturnResponseArticleDto() throws Exception {
        ArticleResponse responseDto = ArticleResponse.builder().pageSize(10).last(true).pageNo(1).articleContent(Arrays.asList(articleDto)).build();
        when(articleService.getAllArticles(1, 10)).thenReturn((Page<ArticleDto>) responseDto);

        ResultActions response = mockMvc.perform(get("/news_portal/article")
                .contentType(MediaType.APPLICATION_JSON)
                .param("pageNo", "1")
                .param("pageSize", "10"));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.size()", CoreMatchers.is(responseDto.getArticleContent().size())));
    }

    @Test
    public void ArticleDetail_ShouldReturnArticleDto() throws Exception {
        long articleId = 1L;
        when(articleService.getArticleById(articleId)).thenReturn(articleDto);

        ResultActions response = mockMvc.perform(get("/news_portal/article/{id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(articleDto)));

        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", CoreMatchers.is(articleDto.getTitle())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.preview", CoreMatchers.is(articleDto.getPreview())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", CoreMatchers.is(articleDto.getContent())));
    }

    @Test
    public void updateArticle_ShouldReturnArticleDto() throws Exception {
        long articleId = 1L;
        when(articleService.updateArticle(articleId, articleDto)).thenReturn(articleDto);

        ResultActions response = mockMvc.perform(put("/news_portal/article/1/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(articleDto)));

        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", CoreMatchers.is(articleDto.getTitle())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.preview", CoreMatchers.is(articleDto.getPreview())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", CoreMatchers.is(articleDto.getContent())));
    }
    @Test
    public void deleteArticle_ShouldReturnMessage() throws Exception {
        long articleId = 1L;
        doNothing().when(articleService).deleteArticle(articleId);

        ResultActions response = mockMvc.perform(delete("/news_portal/article/1/delete")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk());
    }
}