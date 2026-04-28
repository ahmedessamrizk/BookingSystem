package com.demo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final UserDetailsService userDetailsService;
    @Value("${SECRET_KEY}")
    private String SECRET_KEY;
    @Value("${EXPIRATION_TOKEN_TIME}")
    private int EXPIRATION_TOKEN_TIME;

    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal)  authentication.getPrincipal();

        return Jwts.builder()
                .subject(userPrincipal.getUsername())
                .claim("roles", userPrincipal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .claim("userId", userPrincipal.getId())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TOKEN_TIME))
                .signWith(getKey())
                .compact();
    }

    private Key getKey(){
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public UserDetails validateToken(String token) throws IllegalAccessException {
        Claims claims = extractAllClaims(token);

        String username = claims.getSubject();
        if (username == null || username.isBlank()) {
            throw new JwtException("Invalid token: missing subject");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (!userDetails.isEnabled()) {
            throw new IllegalAccessException("Account disabled");
        }

        return userDetails;
    }

    public Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith((SecretKey) getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
