package com.news.portal.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "portal_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false, unique = true)
    private Long id;

    @Size(min = 4, max = 30, message = "{validation.user.loginSize}")
    @Column(name = "login", nullable = false, unique = true)
    private String login;

    @Size(min = 7, max = 60, message = "{validation.user.passwordSize}")
    @Column(name = "password", nullable = false)
    private String password;

    @Size(min = 1, max = 50, message = "{validation.user.nameNotBlank}")
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank(message = "{validation.user.emailNotBlank}")
    @Email(message = "{validation.user.emailValid}")
    @Column(name = "email", nullable = false, unique = true, length = 50)
    private String email;

    @Column(name = "registered_date", insertable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private  Date registered_date;

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private Set<Article> articles;

    public User() {
        registered_date = new Date();
    }

}
