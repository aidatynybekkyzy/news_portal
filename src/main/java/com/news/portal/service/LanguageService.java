package com.news.portal.service;

import com.news.portal.model.Language;

import java.util.Optional;

public interface LanguageService {
    Long getLanguageIdByLocale();

    Optional<Language> getLanguageByLocale();
}
