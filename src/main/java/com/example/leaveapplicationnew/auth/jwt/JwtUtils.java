package com.example.leaveapplicationnew.auth.jwt;

import com.example.leaveapplicationnew.entity.ApplicationUser;
import com.example.leaveapplicationnew.entity.ApplicationUserRole;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtUtils {
    private final SecretKey secretKey;

    public JwtUtils(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    public String generateToken(ApplicationUser user) {
        long nowMillis = System.currentTimeMillis();
        long expMillis = nowMillis + 1000 * 60 * 60 * 24 ; // 24 hour expiry
        Date now = new Date(nowMillis);
        Date exp = new Date(expMillis);

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim(JwtConfig.AUTHORITIES, user
                        .getRoles().stream()
                        .map(ApplicationUserRole::getName)
                        .collect(Collectors.toList()))                  // setting the authority
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Key getKey() {
        return secretKey;
    }
}
