package com.news.portal.controller;

import com.news.portal.dto.ArticleDto;
import com.news.portal.exception.BatchDeleteException;
import com.news.portal.entity.Message;
import com.news.portal.entity.UserEntity;
import com.news.portal.security.LoggedInUser;
import com.news.portal.service.ArticleService;
import com.news.portal.mapper.UserMapper;
import com.news.portal.service.LanguageService;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/news_portal/article")
@ComponentScan("com.news.portal.service")
public class ArticleController {

    private final UserMapper userMapper;

    private final ArticleService articleService;
    private final LanguageService languageService;

    @Autowired
    public ArticleController(ArticleService articleService,
                             UserMapper userMapper,
                             LanguageService languageService) {
        this.articleService = articleService;
        this.userMapper = userMapper;
        this.languageService = languageService;
    }

    @GetMapping("/{articleId}")
    @PreAuthorize("hasRole('ADMIN') or isAuthenticated()")
    public ResponseEntity<ArticleDto> getArticleLocale(@PathVariable("articleId") Long articleId) {
        log.info("Inside getById method ");
        return new ResponseEntity<>(articleService.getArticleByIdLocale(articleId), HttpStatus.OK);
    }

    @GetMapping("/{articleId}/{langCode}")
    @PreAuthorize("hasRole('ADMIN') or isAuthenticated()")
    public ResponseEntity<ArticleDto> getArticleByLangCode(@PathVariable("articleId") Long articleId,
                                                           @PathVariable("langCode") String langCode) {
        log.info("Inside getById method ");
        return new ResponseEntity<>(articleService.getArticleByIdAndLangCode(articleId, langCode), HttpStatus.OK);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("hasRole('ADMIN') or isAuthenticated()")
    public ResponseEntity<Page<ArticleDto>> getAllArticlesLocale(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
    ) {
        Page<ArticleDto> newsPage = articleService.getAllArticlesLocale(pageNo, pageSize);
        return ResponseEntity.ok(newsPage);
    }

    @GetMapping("/all/{langCode}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("hasRole('ADMIN') or isAuthenticated()")
    public ResponseEntity<Page<ArticleDto>> getAllArticlesByLangCode(
            @PathVariable("langCode") String langCode,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
    ) {
        Page<ArticleDto> newsPage = articleService.getAllArticlesByLangCode(pageNo, pageSize, langCode);
        return ResponseEntity.ok(newsPage);
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
   // @PreAuthorize("hasRole('ADMIN') or isAuthenticated()")
    public ResponseEntity<ArticleDto> createArticle(@RequestBody ArticleDto articleDto,
                                                    @LoggedInUser UserEntity user) {
        if (articleDto.getLangCode() != null) {
            articleDto.setLangCode(articleDto.getLangCode());
        } else {
            articleDto.setLangCode(languageService.getDefaultLanguage().getCode());
        }
        articleDto.setAuthor(userMapper.toDto(user));
        articleService.createArticle(articleDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(articleDto);
    }

    @PatchMapping("/{id}/update")
   // @PreAuthorize("hasRole('ADMIN') or isAuthenticated()")
    public ResponseEntity<ArticleDto> updateArticle(@PathVariable("id") Long articleId,
                                                    @RequestBody ArticleDto articleDto) {
        ArticleDto response = articleService.updateArticle(articleId, articleDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{articleId}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Message> deleteArticle(@PathVariable("articleId") Long articleId) {

        return new ResponseEntity<>(articleService.deleteArticle(articleId), HttpStatus.valueOf(HttpStatus.OK.value()));
    }

    @DeleteMapping("/batch-delete")
    //@PreAuthorize("hasRole('ADMIN') or isAuthenticated()")
    public ResponseEntity<String> deleteInBatchArticles(@RequestParam("ids") String[] ids) {
        List<Long> articleIds = Arrays.stream(ids).mapToLong(Long::parseLong).boxed().toList();
        try {
            articleService.deleteInBatch(articleIds);
            return ResponseEntity.ok("Articles deleted successfully.");
        } catch (BatchDeleteException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete articles. Reason: " + e.getMessage());
        }
    }

    @GetMapping("hello")
    public String hello() {
        return "Hello from News Portal App";
    }

}
