package com.news.portal.repository;

import com.news.portal.model.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
   Article findArticleById(Long authorId);
   Page<Article> findAllByOrderByCreatedDate(Pageable pageable);

    Optional<Article> findByTitle(String title);
}
