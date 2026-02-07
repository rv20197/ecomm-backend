package com.vatsalrajgor.eCommerce.controller;

import com.vatsalrajgor.eCommerce.model.AppRole;
import com.vatsalrajgor.eCommerce.model.Role;
import com.vatsalrajgor.eCommerce.model.User;
import com.vatsalrajgor.eCommerce.repository.RoleRepository;
import com.vatsalrajgor.eCommerce.repository.UserRepository;
import com.vatsalrajgor.eCommerce.security.request.LoginRequest;
import com.vatsalrajgor.eCommerce.security.request.SignUpRequest;
import com.vatsalrajgor.eCommerce.security.response.MessageResponse;
import com.vatsalrajgor.eCommerce.security.response.UserInfoResponse;
import com.vatsalrajgor.eCommerce.security.services.UserDetailsImpl;
import com.vatsalrajgor.eCommerce.security.utils.JwtUtils;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.function.ServerRequest;

import java.util.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @PostMapping("/sign-in")
    public ResponseEntity<?> signInUser(@RequestBody LoginRequest loginRequest){
        Authentication authentication;
        try{
            authentication = this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            assert userDetails != null;
            ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
            List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
            UserInfoResponse userInfoResponse = new UserInfoResponse(userDetails.getUserId(), userDetails.getUsername(),roles);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.SET_COOKIE, jwtCookie.toString());
            return new ResponseEntity<Object>(userInfoResponse, headers, HttpStatus.OK);
        } catch (AuthenticationException e){
            Map<String,Object> map = new HashMap<>();
            map.put("message",e.getMessage());
            map.put("error","Authentication Failed");
            map.put("status",false);
            return new ResponseEntity<Object>(map, HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUpUser(@Valid @RequestBody SignUpRequest signUpRequest){
        if(userRepository.existsByUserName(signUpRequest.getUsername())){
            return new ResponseEntity<Object>(new MessageResponse("Username already exists!"), HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())){
            return new ResponseEntity<Object>(new MessageResponse("Email already exists!"), HttpStatus.BAD_REQUEST);
        }

        if(!signUpRequest.getPassword().equals(signUpRequest.getConfirmPassword())){
            return new ResponseEntity<Object>(new MessageResponse("Passwords do not match!"), HttpStatus.BAD_REQUEST);
        }

        String encodedPassword = passwordEncoder.encode(signUpRequest.getPassword());

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if(strRoles==null){
            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER).orElseThrow(()->new RuntimeException("Role is not found!"));
            roles.add(userRole);
        } else {
            strRoles.forEach(role->{
                switch (role.toLowerCase()){
                    case "admin":
                        Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN).orElseThrow(()->new RuntimeException("Role is not found!"));
                        roles.add(adminRole);
                        break;
                    case "seller":
                        Role sellerRole = roleRepository.findByRoleName(AppRole.ROLE_SELLER).orElseThrow(()->new RuntimeException("Role is not found!"));
                       roles.add(sellerRole);
                        break;
                    case "customer":
                        Role customerRole = roleRepository.findByRoleName(AppRole.ROLE_CUSTOMER).orElseThrow(()->new RuntimeException("Role is not found!"));
                        roles.add(customerRole);
                        break;
                    case "guest":
                        Role guestRole = roleRepository.findByRoleName(AppRole.ROLE_GUEST).orElseThrow(()->new RuntimeException("Role is not found!"));
                        roles.add(guestRole);
                        break;
                    default:
                        Role userDefaultRole = roleRepository.findByRoleName(AppRole.ROLE_USER).orElseThrow(()->new RuntimeException("Role is not found!"));
                        roles.add(userDefaultRole);
                        break;

                }
            });
        }

        User newUser = new User(
                signUpRequest.getUsername(),
                encodedPassword,
                signUpRequest.getFirstName(),
                signUpRequest.getLastName(),
                signUpRequest.getEmail(),
                signUpRequest.getPhoneNumber(),
                true
        );
        newUser.setRoles(roles);
        userRepository.save(newUser);
        return new ResponseEntity<Object>(new MessageResponse("User Registered successfully!"), HttpStatus.OK);
    }
}
