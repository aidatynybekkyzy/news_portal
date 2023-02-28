package com.news.portal.service;


import com.news.portal.model.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserEntity getByUsername(String  username);

}
