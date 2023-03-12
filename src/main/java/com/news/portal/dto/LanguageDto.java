package com.news.portal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LanguageDto {
    private Long id;
    private String code;
    private Set<ArticleDto> articleDtosSet;
}
