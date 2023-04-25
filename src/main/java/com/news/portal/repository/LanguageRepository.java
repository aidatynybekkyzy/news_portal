package com.news.portal.repository;

import com.news.portal.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Long> {
    @Transactional
    @Query("select l.id from Language l where l.code = :code")
    Long findIdByCode(String code);

    @Transactional
    @Query("select l from Language l where l.code = ?1")
    Optional<Language> findByCode(String code);


}
