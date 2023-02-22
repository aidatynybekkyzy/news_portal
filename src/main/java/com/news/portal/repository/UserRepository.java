package com.news.portal.repository;

import com.news.portal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsUserByUsername(String username);

    Optional<User> findUserByUsername(String username);


}
