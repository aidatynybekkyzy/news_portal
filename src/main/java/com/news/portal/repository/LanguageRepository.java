package com.news.portal.repository;

import com.news.portal.model.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Long> {
    Optional<Language> findByCode(String languageCode);
    @Query("select l.id from Language l where l.code = :code")
    Long findIdByCode (String code);



}
