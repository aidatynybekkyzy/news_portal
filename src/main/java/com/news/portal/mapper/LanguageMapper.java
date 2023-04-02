package com.news.portal.mapper;

import com.news.portal.dto.LanguageDto;
import com.news.portal.entity.Language;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LanguageMapper {
    LanguageDto toDto(Language language);

    Language toEntity(LanguageDto languageDto);
}
