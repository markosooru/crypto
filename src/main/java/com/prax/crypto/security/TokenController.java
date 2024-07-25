package com.prax.crypto.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/token")
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;
    private final BearerTokenResolver bearerTokenResolver = new DefaultBearerTokenResolver();

    @PostMapping("/login")
    public String login(Authentication authentication) {
        return tokenService.generateToken(authentication);
    }

    @PostMapping("/logout")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void logout(HttpServletRequest request) {
        var token = bearerTokenResolver.resolve(request);
        if (token != null) {
            tokenService.blacklistToken(token);
            SecurityContextHolder.clearContext();
        }
    }
}
