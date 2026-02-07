package com.vatsalrajgor.eCommerce.security.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UserInfoResponse {
    private Long userId;
    private String userName;
    private List<String> roles;
    private String token;
}
