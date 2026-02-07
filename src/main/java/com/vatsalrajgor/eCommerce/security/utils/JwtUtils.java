package com.vatsalrajgor.eCommerce.security.utils;

import com.vatsalrajgor.eCommerce.security.services.UserDetailsImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class JwtUtils {
    @Value("${jwt.expiration}")
    private int jwtExpirationInMs;
    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.cookie.name}")
    private String jwtCookie;

    public Key key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

//    public String getJwtFromRequest(HttpServletRequest request){
//        String bearerToken = request.getHeader("Authorization");
//        log.debug("Bearer token: {}",bearerToken);
//        if(bearerToken != null && bearerToken.startsWith("Bearer ")){
//            return bearerToken.substring(7);
//        }
//        return null;
//    }


    public String getJwtFromCookie(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, jwtCookie);
        if (cookie != null) {
            return cookie.getValue();
        }else {
            return null;
        }
    }

    private String generateJwtTokenFromUsername(@NonNull UserDetails userDetails){
        String username = userDetails.getUsername();
        Date expirationInMs = new Date(new Date().getTime() + jwtExpirationInMs);
        Map<String, Object> assignedRole = new HashMap<>();
        assignedRole.put("role", userDetails.getAuthorities());
        return Jwts.builder().subject(username).claims(assignedRole).issuedAt(new Date()).expiration(expirationInMs).signWith(key()).compact();
    }

    public ResponseCookie generateJwtCookie(UserDetailsImpl userDetails) {
        String jwt = generateJwtTokenFromUsername(userDetails);
        return ResponseCookie.from(jwtCookie, jwt)
                .path("/api")
                .maxAge(jwtExpirationInMs)
                .httpOnly(false)
                .build();
    }

    public ResponseCookie getCleanCookie() {
        return ResponseCookie.from(jwtCookie, null)
                .path("/api")
                .build();
    }

    public String getUserNameFromJwtToken(String token){
        return Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public boolean validateJwtToken(String authToken){
        try{
            Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(authToken);
            log.debug("JWT token is valid");
            return true;
        }catch (MalformedJwtException e){
            log.error("Invalid JWT Token: {}", e.getMessage());
        } catch (ExpiredJwtException e){
            log.error("Expired JWT Token: {}", e.getMessage());
        } catch (UnsupportedJwtException e){
            log.error("Unsupported JWT Token: {}", e.getMessage());
        } catch (IllegalArgumentException e){
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
            return false;
    }
}
