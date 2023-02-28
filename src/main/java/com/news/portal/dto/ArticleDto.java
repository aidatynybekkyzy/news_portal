package com.news.portal.dto;

import com.news.portal.model.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDto {
    private Long id;
    private String title;
    private String preview;
    private String content;
    private LocalDateTime createdDate;
    private UserEntity author;

}
