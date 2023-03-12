package com.news.portal.service.impl;

import com.news.portal.model.Language;
import com.news.portal.repository.LanguageRepository;
import com.news.portal.service.LanguageService;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

@Service
public class LanguageServiceImpl implements LanguageService {
    private final LanguageRepository languageRepository;

    public LanguageServiceImpl(LanguageRepository languageRepository) {
        this.languageRepository = languageRepository;
    }

    @Override
    public Long getLanguageIdByLocale() {
        Locale locale = LocaleContextHolder.getLocale();
        String languageCode = locale.getLanguage();
        return languageRepository.findIdByCode(languageCode);
    }

    @Override
    public Optional<Language> getLanguageByLocale() {
        Locale locale = LocaleContextHolder.getLocale();
        String languageCode = locale.getLanguage();
        return languageRepository.findByCode(languageCode);
    }
}
