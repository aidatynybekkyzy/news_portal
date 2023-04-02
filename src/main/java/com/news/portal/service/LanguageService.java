package com.news.portal.service;

import com.news.portal.entity.Language;
import org.springframework.stereotype.Service;

@Service
public interface LanguageService {
    long getLanguageIdByLocale();

    Language getDefaultLanguage();

    Language getLanguageByCode(String langCode);
}
