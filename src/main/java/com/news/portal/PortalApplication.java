package com.news.portal;

import com.news.portal.repository.RoleRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


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

}
