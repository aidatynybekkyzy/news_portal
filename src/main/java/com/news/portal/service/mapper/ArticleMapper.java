package com.news.portal.service.mapper;

import com.news.portal.dto.ArticleDto;
import com.news.portal.model.Article;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ArticleMapper {

    @Mapping(source = "author", target = "author")
    ArticleDto toDto(Article article);

    Article toEntity(ArticleDto articleDto);

}
