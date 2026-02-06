package com.vatsalrajgor.eCommerce.controller;

import com.vatsalrajgor.eCommerce.security.request.LoginRequestDTO;
import com.vatsalrajgor.eCommerce.security.response.UserInfoResponseDTO;
import com.vatsalrajgor.eCommerce.security.services.UserDetailsImpl;
import com.vatsalrajgor.eCommerce.security.utils.JwtUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/sign-in")
    public ResponseEntity<?> signInUser(@RequestBody LoginRequestDTO loginRequest){
        Authentication authentication;
        try{
            authentication = this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            assert userDetails != null;
            String jwtToken = jwtUtils.generateJwtTokenFromUsername(userDetails);
            List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
            UserInfoResponseDTO userInfoResponse = new UserInfoResponseDTO(userDetails.getUserId(), userDetails.getUsername(),roles,jwtToken);
            return new ResponseEntity<Object>(userInfoResponse, HttpStatus.OK);
        } catch (AuthenticationException e){
            Map<String,Object> map = new HashMap<>();
            map.put("message",e.getMessage());
            map.put("error","Authentication Failed");
            map.put("status",false);
            return new ResponseEntity<Object>(map, HttpStatus.UNAUTHORIZED);
        }
    }
}
