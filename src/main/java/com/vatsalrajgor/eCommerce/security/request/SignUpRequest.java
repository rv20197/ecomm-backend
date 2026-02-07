package com.vatsalrajgor.eCommerce.security.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class SignUpRequest {
    @NotBlank
    @Size(min = 1, max = 20)
    private String firstName;
    @NotBlank
    @Size(min = 1, max = 20)
    private String lastName;
    @NotBlank
    @Size(min = 3, max = 20)
    private String username;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Size(min = 6, max = 20)
    private String password;
    @NotBlank
    @Size(min = 6, max = 20)
    private String confirmPassword;
    @NotBlank
    @Size(min = 10, max = 10)
    private String phoneNumber;

    private Set<String> role;
}
