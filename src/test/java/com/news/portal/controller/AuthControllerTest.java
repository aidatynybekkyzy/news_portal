package com.news.portal.controller;

import com.news.portal.dto.AuthenticationRequestDto;
import com.news.portal.dto.RegistrationRequestDto;
import com.news.portal.entity.Role;
import com.news.portal.entity.UserEntity;
import com.news.portal.entity.response.AuthenticationResponseDto;
import com.news.portal.security.jwt.JwtTokenProvider;
import com.news.portal.service.UserService;
import com.news.portal.service.impl.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    @MockBean
    private AuthenticationService authenticationService;

    @Autowired
    WebApplicationContext context;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    @Test
    @DisplayName("Register new user")
    void testRegister() throws Exception {
        RegistrationRequestDto requestDto = new RegistrationRequestDto();
        requestDto.setFirstname("User");
        requestDto.setLastname("Test");
        requestDto.setEmail("user@test.com");
        requestDto.setPassword("password");
        requestDto.setConfirmPassword("password");

        UserEntity userEntity = new UserEntity();
        userEntity.setFirstName(requestDto.getFirstname());
        userEntity.setLastName(requestDto.getLastname());
        userEntity.setEmail(requestDto.getEmail());
        userEntity.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        userEntity.setConfirmPassword(passwordEncoder.encode(requestDto.getConfirmPassword()));
        userEntity.setRole(Set.of(Role.builder().roleName("USER").build()));

        given(authenticationService.register(any(RegistrationRequestDto.class))).willReturn(requestDto);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("user@test.com")))
                .andExpect(jsonPath("$.firstname", is("User")))
                .andExpect(jsonPath("$.lastname", is("Test")))
                .andDo(print());

        verify(authenticationService, times(1)).register(any(RegistrationRequestDto.class));
    }

    @Test
    @DisplayName("Login  user")
    public void testAuthenticate() throws Exception {

        String email = "john.doe@example.com";
        String password = "password123";
        String jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

        AuthenticationRequestDto requestDto = new AuthenticationRequestDto();
        requestDto.setEmail(email);
        requestDto.setPassword(password);

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setEmail(email);

        AuthenticationResponseDto responseDto = new AuthenticationResponseDto();
        responseDto.setAccessToken(jwtToken);

        when(userService.findUserByEmail(requestDto.getEmail())).thenReturn(Optional.of(new UserEntity()));
        when(jwtTokenProvider.generateToken(userEntity)).thenReturn(jwtToken);
        when(authenticationService.authenticate(requestDto)).thenReturn(responseDto);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(responseDto.getAccessToken()))
                .andDo(print());
    }

    @Test
    @DisplayName("Logout user")
    void testLogout() throws Exception {
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    private static String asJsonString(final Object obj) {
        try {
            return new org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}