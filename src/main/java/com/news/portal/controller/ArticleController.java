package com.news.portal.controller;

import com.news.portal.dto.ArticleDto;
import com.news.portal.dto.ArticleResponse;
import com.news.portal.dto.UserDto;
import com.news.portal.model.Message;
import com.news.portal.model.UserEntity;
import com.news.portal.security.config.LoggedInUser;
import com.news.portal.service.ArticleService;
import com.news.portal.service.mapper.ArticleMapper;
import com.news.portal.service.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping(path = "/news_portal/article")
@ComponentScan("com.news.portal.service")
public class ArticleController {
    private final UserMapper userMapper;

    private final ArticleService articleService;

    @Autowired
    public ArticleController(ArticleService articleService,
                             UserMapper userMapper) {
        this.articleService = articleService;
        this.userMapper = userMapper;
    }

    @GetMapping("/{articleId}")
    public ResponseEntity<ArticleDto> getArticle(@PathVariable("articleId") Long articleId) {
        log.info("Inside getById method ");
        return new ResponseEntity<>(articleService.getArticleById(articleId), HttpStatus.OK);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<Page<ArticleDto>> getAllArticles(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
    ) {
        Page<ArticleDto> newsPage = articleService.getAllArticles(pageNo, pageSize );
        return  ResponseEntity.ok(newsPage);
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createArticle(@RequestBody ArticleDto articleDto, @LoggedInUser UserEntity user) {
        articleDto.setAuthor(userMapper.toDto(user));
        return new ResponseEntity<>(articleService.createArticle(articleDto), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/update")
    public ResponseEntity<ArticleDto> updateArticle(@PathVariable("id") Long articleId,
                                                    @RequestBody ArticleDto articleDto) {
        ArticleDto response = articleService.updateArticle(articleId, articleDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Message> deleteArticle(@PathVariable("id") Long articleId) {

        return new ResponseEntity<>(articleService.deleteArticle(articleId), HttpStatus.OK);
    }

    @GetMapping("hello")
    public String hello() {
        return new String("Hello from News Portal App");
    }

}
