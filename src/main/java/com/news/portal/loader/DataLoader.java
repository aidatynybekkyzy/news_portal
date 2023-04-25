package com.news.portal.loader;

import com.news.portal.entity.Article;
import com.news.portal.entity.Language;
import com.news.portal.entity.Role;
import com.news.portal.entity.UserEntity;
import com.news.portal.repository.ArticleRepository;
import com.news.portal.repository.LanguageRepository;
import com.news.portal.repository.RoleRepository;
import com.news.portal.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {

    private final ArticleRepository articleRepository;
    private final LanguageRepository languageRepository;

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(ArticleRepository articleRepository, LanguageRepository languageRepository, RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.articleRepository = articleRepository;
        this.languageRepository = languageRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        loadArticles();
    }

    private void loadArticles() {
        Role admin = new Role();
        admin.setRoleName("ADMIN");

        Role user = new Role();
        user.setRoleName("USER");

        roleRepository.save(admin);
        roleRepository.save(user);

        Language lang_en = new Language();
        lang_en.setCode("en");

        Language lang_ru = new Language();
        lang_ru.setCode("ru");

        languageRepository.save(lang_en);
        languageRepository.save(lang_ru);

        UserEntity author_admin = new UserEntity();
        author_admin.setId(1L);
        author_admin.setFirstName("Limbo");
        author_admin.setLastName("Limboo");
        author_admin.setEmail("limbo@example.com");
        author_admin.setPassword(passwordEncoder.encode("password"));
        author_admin.setRole(Set.of(admin));

        UserEntity author_user = new UserEntity();
        author_user.setId(2L);
        author_user.setFirstName("User");
        author_user.setLastName("User Last Name");
        author_user.setEmail("user@example.com");
        author_user.setPassword(passwordEncoder.encode("password"));
        author_user.setRole(Set.of(user));

        userRepository.save(author_admin);
        userRepository.save(author_user);

        List<Article> articlesEn = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Article article = new Article();
            article.setId((long) i);
            article.setTitle("Article title " + i);
            article.setPreview("Article preview " + i + ".");
            article.setContent("Article content " + i + ".");
            article.setPublishedDate(LocalDate.now().minusDays(i));
            article.setLanguage(lang_en);
            article.setAuthor(author_user);

            articlesEn.add(article);
        }

        List<Article> articlesRu = new ArrayList<>();
        for (int i = 4; i <= 7; i++) {
            Article article = new Article();
            article.setId((long) i);
            article.setTitle("Залоговок статьи " + i + ".");
            article.setPreview("Превью статьи " + i + ".");
            article.setContent("Контент статьи " + i + ".");
            article.setPublishedDate(LocalDate.now().minusDays(i));
            article.setLanguage(lang_ru);
            article.setAuthor(author_user);

            articlesRu.add(article);
        }

        articleRepository.saveAll(articlesEn);
        articleRepository.saveAll(articlesRu);
    }
}
