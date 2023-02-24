package com.news.portal.service;


import com.news.portal.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User getByUsername(String  username);

}
