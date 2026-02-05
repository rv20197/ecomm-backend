package com.vatsalrajgor.eCommerce.security.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LoginResponse {
    private String token;
    private String userName;
    private List<String> roles;

    public LoginResponse(String token, String userName, List<String> roles) {
        this.token = token;
        this.userName = userName;
        this.roles = roles;
    }
}
