package com.news.portal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LanguageDto {
    private long id;
    @Size(min = 2, max = 2, message = "Language code should be 2 characters")
    private String code;

}
