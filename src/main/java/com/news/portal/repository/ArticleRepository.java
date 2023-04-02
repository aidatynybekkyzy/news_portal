package com.news.portal.repository;

import com.news.portal.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    @Query("select a from Article a where a.id in ?1")
    List<Article> findByIdIn(List<Long> articleIds);


    @Query("select a from Article a where a.id = ?1 and a.language.code = ?2")
    Optional<Article> findByIdAndLanguage_Code(Long articleId, String langCode);

    @Query("select a from Article a where a.language.id = ?1 order by a.publishedDate")
    Page<Article> findAllByLanguage_IdOrderByPublishedDate(Pageable pageable, long languageId);
    @Query("select a from Article a where a.language.code = ?1 order by a.publishedDate")
    Page<Article> findAllByLanguage_CodeOrderByPublishedDate(Pageable pageable, String langCode);

    @Query("select a from Article a where a.title = ?1 and a.language.code = ?2")
    Optional<Article> findByTitleAndLanguage_Code(String title, String langCode);

    @Query("select a from Article a where a.id = ?1 and a.language.id = ?2")
    Optional<Article> findByIdAndLanguageId(Long articleId, Long languageId);


}
