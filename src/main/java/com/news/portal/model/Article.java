package com.news.portal.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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


    @Column(name = "title", nullable = false)
    @Size(min = 2, max = 100, message = "Title should be from 2 to 100 characters")
    private String title;

    @Column(name = "preview", nullable = false)
    @Size(min = 10, max = 500, message = "Preview content should be from 10 to 500 characters")
    private String preview;

    @Column(name = "content", nullable = false)
    @Size(min = 20, max = 2048, message = "Content should be from 20 to 2048 characters")
    private String content;

    @Column(name = "publishedDate")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull
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
