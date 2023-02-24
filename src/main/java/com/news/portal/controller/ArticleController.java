package com.news.portal.controller;

import com.news.portal.dto.ArticleDto;
import com.news.portal.dto.ArticleResponse;
import com.news.portal.model.Message;
import com.news.portal.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping(path = "/news_portal/")
@ComponentScan("com.news.portal.service")
public class ArticleController {

    private final ArticleService articleService;

   @Autowired
    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping("article/{articleId}")
    public ResponseEntity<ArticleDto> getArticle(@PathVariable("articleId") Long articleId) {
        log.info("Inside getById method ");
        return new ResponseEntity<>(articleService.getArticleById(articleId), HttpStatus.OK);
    }

    @GetMapping("article")
    public ResponseEntity<ArticleResponse> getAllArticles(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
    ) {
        return new ResponseEntity<>(articleService.getAllArticles(pageNo, pageSize), HttpStatus.OK);
    }

    @PostMapping("article/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Message> createArticle(@RequestBody ArticleDto articleDto) {
        return new ResponseEntity<>(articleService.createArticle(articleDto), HttpStatus.CREATED);
    }

    @PatchMapping("article/{id}/update")
    public ResponseEntity<ArticleDto> updateArticle(@PathVariable("id") Long articleId, @RequestBody ArticleDto articleDto) {
        ArticleDto response = articleService.updateArticle(articleId, articleDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("article/{id}/delete")
    public ResponseEntity<Message> deleteArticle(@PathVariable("id") Long articleId) {

        return new ResponseEntity<>(articleService.deleteArticle(articleId), HttpStatus.OK);
    }

    @GetMapping("hello")
    public String hello() {
        return new String("Hello word");
    }


}
