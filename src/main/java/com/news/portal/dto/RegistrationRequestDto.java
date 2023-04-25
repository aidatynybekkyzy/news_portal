package com.news.portal.dto;

import com.news.portal.entity.UserEntity;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequestDto {
    private String firstname;
    private String lastname;
    private String email;

    @Size(min=4, max=12)
    private String password;
    @Size (min=4, max=12)
    private String confirmPassword;

}
