package com.news.portal.service.impl;
import com.news.portal.entity.Language;
import com.news.portal.exception.LanguageNotFoundException;
import com.news.portal.repository.LanguageRepository;
import com.news.portal.service.LanguageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Locale;
import java.util.Optional;

@Service
public class LanguageServiceImpl implements LanguageService {

    private final LanguageRepository languageRepository;

    @Autowired
    public LanguageServiceImpl(LanguageRepository languageRepository) {
        this.languageRepository = languageRepository;
    }

    @Transactional
    @Override
    public long getLanguageIdByLocale() {
        Locale locale = LocaleContextHolder.getLocale();
        String langCode = locale.getLanguage();
        return languageRepository.findIdByCode(langCode);
    }


    @Override
    public Language getDefaultLanguage() {
        Optional<Language> language = languageRepository.findByCode("ru");
        return language.orElseThrow(() -> new LanguageNotFoundException(language.get().getCode()));
    }

    @Override
    public Language getLanguageByCode(String langCode) {
        return languageRepository.findByCode(langCode).orElse(getDefaultLanguage());
    }
}