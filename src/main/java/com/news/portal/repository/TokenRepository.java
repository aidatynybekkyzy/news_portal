package com.news.portal.repository;

import com.news.portal.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    @Transactional
    @Query(value = """
            select t from Token t inner join UserEntity u\s
            on t.user.id = u.id\s
            where u.id = :id and (t.expired = false or t.revoked = false)\s
            """)
    List<Token> findAllValidTokenByUser(Long id);
    @Transactional
    @Query("select t from Token t where t.token = ?1")
    Optional<Token> findByToken(String token);
}
