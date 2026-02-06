package com.vatsalrajgor.eCommerce.security.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
