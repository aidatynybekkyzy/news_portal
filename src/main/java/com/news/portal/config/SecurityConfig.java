package com.news.portal.config;

import com.news.portal.entity.Role;
import com.news.portal.security.jwt.JWTAuthFilter;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JWTAuthFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;

    private static final String AUTH_END_POINT = "/auth/**";
    private static final String ARTICLE_END_POINT = "/news_portal/article/**";
    private static final String ADMIN = Role.builder()
            .roleName("ADMIN").build().getRoleName();
    private static final String USER = Role.builder()
            .roleName("USER").build().getRoleName();


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .authorizeHttpRequests()
                .antMatchers(HttpMethod.GET, ARTICLE_END_POINT).permitAll()
               // .antMatchers(HttpMethod.POST, ARTICLE_END_POINT).hasAnyAuthority(USER, ADMIN)
               // .antMatchers(HttpMethod.PATCH, ARTICLE_END_POINT).hasAnyAuthority(USER, ADMIN)
                // .antMatchers(HttpMethod.DELETE, ARTICLE_END_POINT).hasAnyAuthority(USER,ADMIN)
                .antMatchers(AUTH_END_POINT).permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout()
                .logoutUrl("/auth/logout")
                .addLogoutHandler(logoutHandler)
                .logoutSuccessHandler((request, response, authentication) ->
                        SecurityContextHolder.clearContext());

        return http.build();
    }
}
