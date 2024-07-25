package com.prax.crypto.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private JwtDecoder jwtDecoder;

    @Mock
    private Authentication authentication;

    @Mock
    private Jwt jwt;

    @Test
    void generateToken_givenAuthentication_generatesToken() {
        // mock
        when(authentication.getName()).thenReturn("testuser@example.com");
        when(authentication.getAuthorities()).thenAnswer(invocation -> List.of((GrantedAuthority) () -> "ROLE_USER"));
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);
        when(jwt.getTokenValue()).thenReturn("testtoken");

        // when
        var result = tokenService.generateToken(authentication);

        // then
        assertEquals("testtoken", result);
        verify(jwtEncoder).encode(any(JwtEncoderParameters.class));
    }

    @Test
    void blacklistToken_givenToken_blacklistsToken() {
        // given
        String token = "testtoken";

        // when
        tokenService.blacklistToken(token);

        // then
        Set<String> blacklistedTokens = getBlacklistedTokens();
        assertThat(blacklistedTokens).contains(token);
    }

    @Test
    void isTokenBlacklisted_givenToken_returnsTrueIfTokenIsBlacklisted() {
        // given
        String token = "testtoken";
        tokenService.blacklistToken(token);

        // when
        boolean result = tokenService.isTokenBlacklisted(token);

        // then
        assertTrue(result);
    }

    @Test
    void isTokenBlacklisted_givenToken_returnsFalseIfTokenIsNotBlacklisted() {
        // given
        String token = "testtoken";

        // when
        boolean result = tokenService.isTokenBlacklisted(token);

        // then
        assertFalse(result);
    }

    @Test
    void validateToken_givenValidToken_returnsJwt() {
        // given
        String token = "validtoken";

        // mock
        when(jwtDecoder.decode(token)).thenReturn(jwt);

        // when
        var result = tokenService.validateToken(token);

        // then
        assertEquals(jwt, result);
        verify(jwtDecoder).decode(token);
    }

    @Test
    void validateToken_givenBlacklistedToken_throwsRuntimeException() {
        // given
        String token = "blacklistedtoken";
        tokenService.blacklistToken(token);

        // when & then
        var exception = assertThrows(RuntimeException.class, () -> tokenService.validateToken(token));
        assertEquals("Token is blacklisted", exception.getMessage());
    }

    private Set<String> getBlacklistedTokens() {
        try {
            var field = TokenService.class.getDeclaredField("blacklistedTokens");
            field.setAccessible(true);
            return (Set<String>) field.get(tokenService);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
