package com.news.portal.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@Table(name = "languages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Language {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "language_seq_gen")
    @SequenceGenerator(name = "language_seq_gen", sequenceName = "language_sequence", allocationSize = 1)
    private long id;

    @Size(min = 2, max = 2, message = "Language code should be 2 characters")
    @Column(nullable = false, unique = true)
    private String code;

    @JsonIgnore
    @OneToMany(mappedBy = "language")
    private Set<Article> articles;

    public Language(String en) {
        this.code = en;
    }
    public Language(long id){
        this.id = id;
    }

    public Language(long id, String en) {
        this.id =  id;
        this.code = en;
    }
}
