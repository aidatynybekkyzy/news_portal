package com.news.portal.repository;

import com.news.portal.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Boolean existsUserByEmail(String username);

    Optional<UserEntity> findByEmail(String email);


}
