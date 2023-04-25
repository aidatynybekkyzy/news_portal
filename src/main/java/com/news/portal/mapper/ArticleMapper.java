package com.news.portal.mapper;

import com.news.portal.dto.ArticleDto;
import com.news.portal.entity.Article;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ArticleMapper {
    ArticleMapper INSTANCE = Mappers.getMapper(ArticleMapper.class);

    @Mapping(source = "language.code", target = "langCode")
    ArticleDto toDto(Article article);


}
