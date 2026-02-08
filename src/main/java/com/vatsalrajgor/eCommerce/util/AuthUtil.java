package com.vatsalrajgor.eCommerce.util;

import com.vatsalrajgor.eCommerce.model.User;
import com.vatsalrajgor.eCommerce.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AuthUtil {
    private final UserRepository userRepository;

    private User getUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUserName(authentication.getName()).orElseThrow(()->new UsernameNotFoundException("%s not found".formatted(authentication.getName())));
    }

    public String loggedInEmail(){
        User user = getUser();
        return user.getEmail();
    }

    public Long loggedInUserId() {
        User user = getUser();
        return user.getUserId();
    }

    public User getLoggedInUser(){
        return getUser();
    }
}
