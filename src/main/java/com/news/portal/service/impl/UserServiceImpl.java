package com.news.portal.service.impl;


import com.news.portal.entity.UserEntity;
import com.news.portal.repository.UserRepository;
import com.news.portal.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Transactional
    @Override
    public Optional<UserEntity> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    @Transactional
    @Override
    public boolean userAuthentificated(String email, String password) {
        String databasePassword = userRepository.findPasswordByEmail(email);
        return passwordEncoder.matches(password, databasePassword);
    }
}
