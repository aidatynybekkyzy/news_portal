package com.news.portal.service;

import com.news.portal.model.Article;
import com.news.portal.model.User;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ArticleServiceImplTest {

    @Test
    void CanAddArticle() {
       //given
       Article article = new Article(
               "В Бишкеке разработали новые автобусные маршруты. Схема",
               "В Бишкеке разработали новые автобусные маршруты. В департаменте " +
                       "транспорта и развития дорожно-транспортной инфраструктуры " +
                       "мэрии предоставили 24.kg схему.",
               "Маршрут № 31 — жилмассив «Ынтымак» — кольцевой, протяженность 34 километра, " +
                       "интервал — 7 минут. Выделено 24 автобуса.", new User()
       );
    }

    @Test
    @Disabled
    void getArticleById() {
    }

    @Test
    @Disabled
    void updateArticle() {
    }

    @Test
    @Disabled
    void deleteArticle() {
    }

    @Test
    @Disabled
    void getAllArticlesByUserId() {
    }
}