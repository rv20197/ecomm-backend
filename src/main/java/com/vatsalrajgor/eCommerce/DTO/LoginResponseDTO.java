package com.vatsalrajgor.eCommerce.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LoginResponseDTO {
    private String token;
    private String userName;
    private List<String> roles;

    public LoginResponseDTO(String token, String userName, List<String> roles) {
        this.token = token;
        this.userName = userName;
        this.roles = roles;
    }
}
