package com.news.portal.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "YYYY-MM-DD hh:mm:ss")
    private LocalDateTime publishedDate;

    private String langCode;
    private UserDto author;

}
