package com.news.portal.controller;

import com.news.portal.dto.AuthenticationRequestDto;
import com.news.portal.dto.RegistrationRequestDto;
import com.news.portal.entity.response.AuthenticationResponseDto;
import com.news.portal.exception.PasswordIncorrectException;
import com.news.portal.service.impl.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Slf4j
public class AuthController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<RegistrationRequestDto> register(
            @RequestBody RegistrationRequestDto registerDto) throws PasswordIncorrectException {
        log.info("Doing registration of user " + registerDto.getEmail());
        return ResponseEntity.ok(authenticationService.register(registerDto));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDto> authenticate(
            @RequestBody AuthenticationRequestDto authenticationRequestDto) {
        log.info("Doing authentication of user " + authenticationRequestDto.getEmail());
        return ResponseEntity.ok(authenticationService.authenticate(authenticationRequestDto));
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
        securityContextLogoutHandler.logout(request, response, null);
    }

}
