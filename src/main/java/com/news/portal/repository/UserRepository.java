package com.news.portal.repository;

import com.news.portal.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    @Query("select (count(u) > 0) from UserEntity u where u.email = ?1")
    Boolean existsUserByEmail(String username);

    @Query("select u from UserEntity u where u.email = ?1")
    Optional<UserEntity> findByEmail(String email);

    @Query("select u.password from UserEntity u where u.email = :email")
    String findPasswordByEmail(String email);


}
