package com.prax.crypto.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TokenService {

    @Value("${spring.security.jwt.expiration}")
    private int expiration;

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    private final Set<String> blacklistedTokens = new HashSet<>();

    public String generateToken(Authentication authentication) {
        var now = Instant.now();

        var roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith("ROLE_"))
                .toList();

        var claims = JwtClaimsSet.builder()
                .issuer("crypto")
                .issuedAt(now)
                .expiresAt(now.plus(expiration, ChronoUnit.MINUTES))
                .subject(authentication.getName())
                .claim("roles", roles)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public void blacklistToken(String token) {
        blacklistedTokens.add(token);
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }

    public Jwt validateToken(String token) {
        if (isTokenBlacklisted(token)) {
            throw new RuntimeException("Token is blacklisted");
        }
        return jwtDecoder.decode(token);
    }
}