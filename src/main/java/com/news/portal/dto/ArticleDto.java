package com.news.portal.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.news.portal.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDto {
    private Long id;
    @NotNull
    @Size(min = 2, max = 100, message = "Title should be from 2 to 100 characters")
    private String title;

    @NotNull
    @Size(min = 10, max = 500, message = "Preview content should be from 10 to 500 characters")
    private String preview;
    @NotNull
    @Size(min = 20, max = 2048, message = "Content should be from 20 to 2048 characters")
    private String content;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate publishedDate;

    @NotNull
    private String langCode;
    private UserEntity author;

}
