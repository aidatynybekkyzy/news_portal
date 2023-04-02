package com.news.portal.service;

import com.news.portal.entity.UserEntity;

import java.util.Optional;

public interface UserService {

    Optional<UserEntity> findUserByEmail(String email);

    boolean userAuthentificated(String email, String password);
}
