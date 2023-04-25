package com.news.portal.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "article")
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "article_seq_gen")
    @SequenceGenerator(name = "article_seq_gen", sequenceName = "article_sequence", allocationSize = 1)
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
    @CreatedDate
    @Column(name = "published_date")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "YYYY-MM-DD")
    @NotNull
    private LocalDate publishedDate;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "language_id")
    private Language language;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id", nullable = false, updatable = false, referencedColumnName = "id")
    private UserEntity author;

    public Article(long id, String title, String preview, String content, LocalDate date) {
        this.id = id;
        this.title = title;
        this.preview = preview;
        this.content = content;
        this.publishedDate = date;
    }
}
