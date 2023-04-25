package com.news.portal.controller;

import com.news.portal.dto.ArticleDto;
import com.news.portal.entity.UserEntity;
import com.news.portal.exception.ArticleNotFoundException;
import com.news.portal.exception.BatchDeleteException;
import com.news.portal.service.ArticleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(SpringExtension.class)
class ArticleControllerTest {
    @MockBean
    private ArticleService articleService;
    private MockMvc mockMvc;
    final WebApplicationContext context;

    private final static long ID = 1L;
    private final static String TITLE = "Test title ";
    private final static String PREVIEW = "Test preview ";
    private final static String CONTENT = "Test content ";
    private final static String LANG_EN = "en";

    private final static int PAGE_NO = 0;
    private final static int PAGE_SIZE = 10;


    ArticleControllerTest(WebApplicationContext context) {
        this.context = context;
    }

    final ArticleDto mockArticle = ArticleDto.builder()
            .id(ID)
            .title(TITLE)
            .preview(PREVIEW)
            .content(CONTENT)
            .publishedDate(LocalDate.now())
            .langCode(LANG_EN)
            .build();


    final Page<ArticleDto> mockArticlePage = new PageImpl<>(Collections.singletonList(mockArticle));

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Get Article by Id Locale")
    void getArticleLocale_success() throws Exception {
        when(articleService.getArticleByIdLocale(ID)).thenReturn(mockArticle);
        mockMvc.perform(get("/news_portal/article/{articleId}", ID)).
                andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockArticle.getId()))
                .andExpect(jsonPath("$.title").value(mockArticle.getTitle()))
                .andExpect(jsonPath("$.preview").value(mockArticle.getPreview()))
                .andExpect(jsonPath("$.content").value(mockArticle.getContent()))
                .andExpect(jsonPath("$.langCode").value(mockArticle.getLangCode()))
                .andDo(print());
    }

    @Test
    @DisplayName("Get Article by Id and LangCode")
    void getArticleByLangCode() throws Exception {
        when(articleService.getArticleByIdAndLangCode(ID, LANG_EN)).thenReturn(mockArticle);
        mockMvc.perform(get("/news_portal/article/{articleId}/{langCode}", ID, LANG_EN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID))
                .andExpect(jsonPath("$.title").value(mockArticle.getTitle()))
                .andExpect(jsonPath("$.preview").value(mockArticle.getPreview()))
                .andExpect(jsonPath("$.content").value(mockArticle.getContent()))
                .andExpect(jsonPath("$.langCode").value(mockArticle.getLangCode()))
                .andDo(print());
    }

    @Test
    @DisplayName("Get all Articles Locale")
    void getAllArticlesLocale() throws Exception {
        when(articleService.getAllArticlesLocale(PAGE_NO, PAGE_SIZE)).thenReturn(mockArticlePage);
        mockMvc.perform(get("/news_portal/article/all?pageNo={pageNo}&pageSize={pageSize}", PAGE_NO, PAGE_SIZE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(ID))
                .andExpect(jsonPath("$.content[0].title").value(mockArticle.getTitle()))
                .andExpect(jsonPath("$.content[0].preview").value(mockArticle.getPreview()))
                .andExpect(jsonPath("$.content[0].content").value(mockArticle.getContent()))
                .andExpect(jsonPath("$.content[0].langCode").value(mockArticle.getLangCode()))
                .andDo(print());
    }

    @Test
    @DisplayName("Get all Articles by LangCode")
    void getAllArticlesByLangCode() throws Exception {
        when(articleService.getAllArticlesByLangCode(PAGE_NO, PAGE_SIZE, LANG_EN)).thenReturn(mockArticlePage);
        mockMvc.perform(get("/news_portal/article/all/{langCode}?pageNo={pageNo}&pageSize={pageSize}", LANG_EN, PAGE_NO, PAGE_SIZE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(ID))
                .andExpect(jsonPath("$.content[0].title").value(mockArticle.getTitle()))
                .andExpect(jsonPath("$.content[0].preview").value(mockArticle.getPreview()))
                .andExpect(jsonPath("$.content[0].content").value(mockArticle.getContent()))
                .andDo(print());
    }

    @Test
    @DisplayName("Create Article Success")
    @WithMockUser(username = "limbo@example.com", roles = "ADMIN")
    public void testCreateArticle_success() throws Exception {
        UserEntity author = new UserEntity();
        author.setId(1L);
        author.setFirstName("John");
        author.setLastName("Doe");
        author.setEmail("johndoe@example.com");

        ArticleDto articleDto = new ArticleDto();
        articleDto.setId(ID);
        articleDto.setTitle(TITLE);
        articleDto.setPreview(PREVIEW);
        articleDto.setContent(CONTENT);
        articleDto.setAuthor(author);
        articleDto.setLangCode(LANG_EN);

        when(articleService.createArticle(any(ArticleDto.class))).thenReturn((articleDto));

        mockMvc.perform(post("/news_portal/article/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(articleDto))
                        .with(csrf())
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Test title ")))
                .andExpect(jsonPath("$.preview", is("Test preview ")))
                .andExpect(jsonPath("$.content", is("Test content ")))
                .andExpect(jsonPath("$.langCode", is("en")));

        verify(articleService, times(1)).createArticle(any(ArticleDto.class));
    }

    @Test
    @DisplayName("Update Article Success")
    @WithMockUser(username = "limbo@example.com", roles = "ADMIN")
    public void testUpdateArticle_success() throws Exception {
        ArticleDto articleDto = new ArticleDto();
        articleDto.setTitle("Test Article");
        articleDto.setContent("This is a test article.");

        when(articleService.updateArticle(any(ArticleDto.class))).thenReturn(articleDto);

        mockMvc.perform(patch("/news_portal/article/1/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(articleDto))
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(articleDto.getTitle())))
                .andExpect(jsonPath("$.content", is(articleDto.getContent())));

        verify(articleService, times(1)).updateArticle(any(ArticleDto.class));
    }

    @Test
    @DisplayName("Delete Article Success")
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    public void testDeleteArticle_success() throws Exception {
        Long id = 1L;
        mockMvc.perform(delete("/news_portal/article//{articleId}/delete", id)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());
        verify(articleService, times(1)).deleteArticle(id);
    }

    @Test
    @DisplayName("Delete Article Throws ArticleNotFoundException()")
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    public void testDeleteArticle_ThrowsArticleNotFoundException() throws Exception {
        Long id = 1L;

        doThrow(new ArticleNotFoundException("Article with id " + id + " not found")).when(articleService).deleteArticle(ID);

        mockMvc.perform(delete("/news_portal/article//{articleId}/delete", id)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reason", is("Article with id " + id + " not found")));
        verify(articleService, times(1)).deleteArticle(id);
    }


    @Test
    @DisplayName("Delete Articles In Batch Success")
    @WithMockUser(roles = "ADMIN")
    public void testDeleteInBatchArticles_success () throws Exception, BatchDeleteException {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);

        doNothing().when(articleService).deleteInBatch(ids);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/news_portal/article/batch-delete")
                        .param("ids", "1,2,3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Articles deleted successfully."));
    }

    @Test
    @DisplayName("Delete Articles In Batch Unauthorized")
    public void testDeleteInBatchArticles_Unauthorized() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/news_portal/article/batch-delete")
                        .param("ids", "1,2,3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Delete Articles In Batch Thows BatchDeleteException")
    public void testDeleteInBatchArticles_BatchDeleteException() throws Exception, BatchDeleteException {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);

        doThrow(new BatchDeleteException("Failed to delete articles."))
                .when(articleService).deleteInBatch(ids);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/news_portal/article/batch-delete")
                        .param("ids", "1,2,3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Failed to delete articles. Reason: Failed to delete articles."));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Delete Articles In Batch MissingParameter")
    public void testDeleteInBatchArticles_MissingParameter() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/news_portal/article/batch-delete")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Delete Articles In Batch InvalidParameter")
    public void testDeleteInBatchArticles_InvalidParameter() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/news_portal/article/batch-delete")
                        .param("ids", "1,a,3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }



    // Convert object to JSON string
    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}