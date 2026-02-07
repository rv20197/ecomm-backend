package com.vatsalrajgor.eCommerce.security.request;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @Size(min = 6, max = 15)
    private String password;
    @NotBlank
    @Size(min = 6, max = 15)
    private String confirmPassword;
    @NotBlank
    @Size(min = 10, max = 10)
    private String phoneNumber;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private Set<String> role;
}
