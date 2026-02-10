package com.vatsalrajgor.eCommerce.security.utils;

import com.vatsalrajgor.eCommerce.security.services.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        log.debug("Auth token filter invoked for URI: {}", request.getRequestURI());
        try{
            String jwt = jwtUtils.getJwtFromCookie(request);
            log.debug("Is Jwt Present: {}", !jwt.isBlank());
            if(jwtUtils.validateJwtToken(jwt)){
                String username = jwtUtils.getUserNameFromJwtToken(jwt);
                log.debug("Authenticated user: {}", username);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                log.debug("Roles of authenticated user: {}", userDetails.getAuthorities());
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                log.debug("Setting authentication in Security Context");
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("Authentication set successfully");
            }
        } catch (Exception e){
            log.error("Error while setting authentication in Security Context: {}", e.getMessage());
        }
        filterChain.doFilter(request, response);
    }
}
