package com.news.portal.service.impl;

import com.news.portal.dto.AuthenticationRequestDto;
import com.news.portal.entity.*;
import com.news.portal.entity.response.AuthenticationResponseDto;
import com.news.portal.dto.RegistrationRequestDto;
import com.news.portal.exception.PasswordIncorrectException;
import com.news.portal.exception.UserNotFoundException;
import com.news.portal.mapper.UserMapper;
import com.news.portal.repository.TokenRepository;
import com.news.portal.repository.UserRepository;
import com.news.portal.security.jwt.JwtTokenProvider;
import com.news.portal.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.Set;


@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService {

    private final JwtTokenProvider jwtTokenProvider;

    private final TokenRepository tokenRepository;

    private final UserService userService;

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    private final UserMapper userMapper;

    public RegistrationRequestDto register(RegistrationRequestDto request) throws PasswordIncorrectException {
        log.info("Registering new user with email " + request.getEmail());
        if (!request.getConfirmPassword().equals(request.getPassword())) {
            throw new PasswordIncorrectException("Passwords do not match");
        }
        var roleUser = Role.builder().roleName("USER").build();
        var user = UserEntity.builder()
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .confirmPassword(passwordEncoder.encode(request.getPassword()))
                .role(Set.of(roleUser))
                .build();
        userRepository.save(user);
        log.info("Saved user with email " + request.getEmail());
        return  userMapper.toDto(user);
    }

    public AuthenticationResponseDto authenticate(@NotNull AuthenticationRequestDto request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword())
            );
            UserEntity user = userService.findUserByEmail(request.getEmail())
                    .orElseThrow(() -> new UserNotFoundException("User does not exist"));
            var jwtToken = jwtTokenProvider.generateToken(user);
            revokeAllUserTokens(user);
            saveUserToken(user, jwtToken);

            return AuthenticationResponseDto.builder()
                    .accessToken(jwtToken)
                    .build();
        } catch (BadCredentialsException exception){
            throw new PasswordIncorrectException("Invalid password");
        }
        catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }

    }

    private void saveUserToken(UserEntity user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(UserEntity user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

}
