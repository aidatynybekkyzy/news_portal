package com.news.portal.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
@Entity
@NoArgsConstructor
@Table(name = "article")
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false, unique = true)
    private Long id;

    @NotBlank(message = "{validation.article.title}")
    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @NotBlank(message = "{validation.article.preview}")
    @Column(name = "preview", nullable = false)
    private String preview;

    @NotBlank(message = "{validation.article.content}")
    @Column(name = "content", nullable = false, length = 65535)
    private String content;

    @Column(name = "createdDate", insertable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")

    private LocalDateTime createdDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private UserEntity author;


    public Article(Long id, String title, String preview, String content, LocalDateTime created, UserEntity author) {
        this.id = id;
        this.title = title;
        this.preview = preview;
        this.content = content;
        this.createdDate = created;
        this.author = author;
    }
}
