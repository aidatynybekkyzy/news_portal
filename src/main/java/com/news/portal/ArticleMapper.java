package com.news.portal;

import com.news.portal.dto.ArticleDto;
import com.news.portal.model.Article;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ArticleMapper {
    ArticleMapper INSTANCE = Mappers.getMapper(ArticleMapper.class);

    @Mapping(source = "author", target = "author")
    ArticleDto toDto(Article article);

    Article toEntity(ArticleDto articleDto);

}
