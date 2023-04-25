package com.news.portal.repository;

import com.news.portal.entity.UserEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    @Transactional
    @Query("select u from UserEntity u where u.email = ?1")
    Optional<UserEntity> findByEmail(String email);
    @Transactional
    @NotNull Optional<UserEntity> findById(@NotNull Long Id);
    @Transactional
    @Query("select u.password from UserEntity u where u.email = :email")
    String findPasswordByEmail(String email);




}
