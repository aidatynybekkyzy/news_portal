package com.news.portal;

import com.news.portal.dto.ArticleDto;
import com.news.portal.model.Role;
import com.news.portal.model.UserEntity;
import com.news.portal.repository.RoleRepository;
import com.news.portal.repository.UserRepository;
import com.news.portal.service.ArticleService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.time.LocalDateTime;


@SpringBootApplication
@EntityScan("com.news.portal")
@EnableJpaRepositories("com.news.portal")
public class PortalApplication {
    private final RoleRepository roleRepository;

    public PortalApplication(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(PortalApplication.class, args);
    }

    @Bean
    CommandLineRunner run(UserRepository userRepository, ArticleService articleService) {
        return args -> {
            roleRepository.save(new Role("USER"));
            roleRepository.save(new Role("ADMIN"));

            UserEntity user1 = new UserEntity(1L, "aidatyn", "password");
            UserEntity user2 = new UserEntity(2L, "aizirek", "password");

            userRepository.save(user1);
            userRepository.save(user2);

            articleService.createArticle(new ArticleDto(1L, "title1", "preview 1", "content1",
                    LocalDateTime.now(), user1));
            articleService.createArticle(new ArticleDto(2L, "title2", "preview 2", "content2",
                    LocalDateTime.now(), user2));

        };
    }

}
