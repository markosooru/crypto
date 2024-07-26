package com.prax.crypto.security;

import com.prax.crypto.account.AppUserDetailsService;
import com.prax.crypto.account.AppUserService;
import com.prax.crypto.account.Role;
import com.prax.crypto.account.dto.AppUserWithRoleDto;
import com.prax.crypto.base.BaseIntTest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TokenControllerIntTest extends BaseIntTest {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private AppUserDetailsService appUserDetailsService;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Test
    @Transactional
    void login_givenValidAuthentication_returnsTokenWithCorrectClaims() throws Exception {
        // given
        var email = "testuser_" + UUID.randomUUID() + "@example.com";
        var password = "testpassword";
        var role = Role.ROLE_USER;
        appUserService.createWithRole(new AppUserWithRoleDto(email, password, role));

        var userDetails = appUserDetailsService.loadUserByUsername(email);
        var authentication = new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        var result = mockMvc.perform(post("/api/token/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authentication)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        var decodedToken = jwtDecoder.decode(result);

        assertThat(decodedToken.getSubject()).isEqualTo(email);
        assertThat(decodedToken.getClaimAsStringList("roles")).contains(role.name());
    }

    @Test
    @Transactional
    void logout_givenValidToken_blacklistsToken() throws Exception {
        // given
        var email = "testuser_" + UUID.randomUUID() + "@example.com";
        var password = "testpassword";
        var role = Role.ROLE_USER;
        appUserService.createWithRole(new AppUserWithRoleDto(email, password, role));

        var userDetails = appUserDetailsService.loadUserByUsername(email);
        var authentication = new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        var token = tokenService.generateToken(authentication);

        // when
        mockMvc.perform(post("/api/token/logout")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        // then
        var isBlacklisted = tokenService.isTokenBlacklisted(token);
        assertThat(isBlacklisted).isTrue();
    }

    @Test
    @Transactional
    void logout_givenNoToken_doesNotBlacklist() throws Exception {
        // when
        mockMvc.perform(post("/api/token/logout"))
                .andExpect(status().isNoContent());

        // then
        assertThat(tokenService.isTokenBlacklisted("")).isFalse();
    }
}
