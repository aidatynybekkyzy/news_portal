package com.news.portal.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@Table(name="languages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Language {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 2, max = 2, message = "Language code should be 2 characters")
    private String code;

    @JsonIgnore
    @OneToMany(mappedBy="language")
    private Set<Article> articleSet;

    public Language(long existingLanguageId) {
        this.id = existingLanguageId;
    }
}
