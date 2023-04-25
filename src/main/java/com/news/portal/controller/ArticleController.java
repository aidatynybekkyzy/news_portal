package com.news.portal.controller;

import com.news.portal.dto.ArticleDto;
import com.news.portal.entity.Message;
import com.news.portal.entity.UserEntity;
import com.news.portal.exception.ArticleAlreadyExistsException;
import com.news.portal.exception.BatchDeleteException;
import com.news.portal.exception.LanguageNotFoundException;
import com.news.portal.exception.UserNotFoundException;
import com.news.portal.security.LoggedInUser;
import com.news.portal.service.ArticleService;
import com.news.portal.service.LanguageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/news_portal/article")
@ComponentScan("com.news.portal.service")
public class ArticleController {

    private final ArticleService articleService;
    private final LanguageService languageService;

    @Autowired
    public ArticleController(ArticleService articleService,
                             LanguageService languageService) {
        this.articleService = articleService;
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
    @PreAuthorize("hasRole('ADMIN') or isAuthenticated()")
    public ResponseEntity<?> createArticle(@RequestBody ArticleDto articleDto,
                                           @LoggedInUser UserEntity user) throws UserNotFoundException {
        try {
            if (articleDto.getLangCode() == null) {
                articleDto.setLangCode(languageService.getDefaultLanguage().getCode());
            }
            articleDto.setAuthor(user);
            articleService.createArticle(articleDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(articleDto);
        } catch (
                ArticleAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Article with the same title already exists");
        } catch (LanguageNotFoundException languageNotFoundException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Language: " + articleDto.getLangCode() + " not found");
        }

    }

    @PatchMapping("/{id}/update")
    @PreAuthorize("hasRole('ADMIN') or isAuthenticated()")
    public ResponseEntity<ArticleDto> updateArticle(@RequestBody ArticleDto articleDto) throws ArticleAlreadyExistsException {
        ArticleDto response = articleService.updateArticle(articleDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{articleId}/delete")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Message> deleteArticle(@PathVariable("articleId") Long articleId) {
        log.info("Deleting article with id " + articleId);
        return new ResponseEntity<>(articleService.deleteArticle(articleId), HttpStatus.valueOf(HttpStatus.OK.value()));
    }

    @DeleteMapping("/batch-delete")
    @PreAuthorize("hasRole('ADMIN') or isAuthenticated()")
    public ResponseEntity<String> deleteInBatchArticles(@RequestParam("ids") List<Long> ids) {
        try {
            articleService.deleteInBatch(ids);
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
