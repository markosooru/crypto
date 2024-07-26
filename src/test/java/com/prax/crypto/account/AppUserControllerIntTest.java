package com.prax.crypto.account;

import com.fasterxml.jackson.core.type.TypeReference;
import com.prax.crypto.account.dto.AppUserDto;
import com.prax.crypto.account.dto.AppUserResponseDto;
import com.prax.crypto.account.dto.AppUserWithRoleDto;
import com.prax.crypto.base.BaseIntTest;
import com.prax.crypto.security.TokenService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AppUserControllerIntTest extends BaseIntTest {

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private AppUserDetailsService appUserDetailsService;

    @Autowired
    private TokenService tokenService;

    @Test
    @Transactional
    void createUser_givenAppUserDto_savesAppUserAndReturnsResponseDto() throws Exception {
        // given
        var email = "testuser_" + UUID.randomUUID() + "@example.com";
        var appUserDto = new AppUserDto(
                email,
                "password"
        );

        // when
        var jsonResponse = mockMvc.perform(post("/api/appuser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appUserDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        var result = objectMapper.readValue(jsonResponse, AppUserResponseDto.class);

        assertThat(result.email()).isEqualTo(appUserDto.email());
        assertThat(result.id()).isNotNull();
        assertThat(result.portfolioItems()).isNull();
    }

    @Test
    void createUser_withDuplicateEmail_returnsConflict() throws Exception {
        // given
        var email = "testuser_" + UUID.randomUUID() + "@example.com";
        var appUserDto = new AppUserDto(email, "password");

        mockMvc.perform(post("/api/appuser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appUserDto)))
                .andExpect(status().isCreated());

        // when
        var jsonResponse = mockMvc.perform(post("/api/appuser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appUserDto)))
                .andExpect(status().isConflict())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        var result = objectMapper.readValue(jsonResponse, new TypeReference<Map<String, String>>() {});

        assertThat(result).containsEntry("error", "Employee with email: " + email + " already exists.");
    }

    @Test
    @Transactional
    void createUser_withEmptyFields_returnsValidationErrors() throws Exception {
        // given
        var appUserDto = new AppUserDto("", "");

        // when
        var jsonResponse = mockMvc.perform(post("/api/appuser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appUserDto)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        var result = objectMapper.readValue(jsonResponse, new TypeReference<Map<String, String>>() {});
        assertThat(result).containsEntry("password", "must not be empty");
        assertThat(result).containsEntry("email", "must not be empty");
    }

    @Test
    @Transactional
    void createUser_withInvalidEmailAndEmptyPassword_returnsValidationErrors() throws Exception {
        // given
        var appUserDto = new AppUserDto("notemail", "");

        // when
        var jsonResponse = mockMvc.perform(post("/api/appuser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appUserDto)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        var result = objectMapper.readValue(jsonResponse, new TypeReference<Map<String, String>>() {});
        assertThat(result).containsEntry("password", "must not be empty");
        assertThat(result).containsEntry("email", "must be a well-formed email address");
    }

    @Test
    @Transactional
    void createUserWithRole_givenAdminTokenAndAppUserWithRoleDto_savesAppUserAndReturnsResponseDto() throws Exception {
        // given
        var adminEmail = "testuser_" + UUID.randomUUID() + "@example.com";
        var adminToken = generateToken(adminEmail, "adminPass", Role.ROLE_ADMIN);

        var email = "testuser_" + UUID.randomUUID() + "@example.com";
        var appUserWithRoleDto = new AppUserWithRoleDto(email, "password", Role.ROLE_ADMIN);

        // when
        var jsonResponse = mockMvc.perform(post("/api/appuser/withrole")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appUserWithRoleDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        var result = objectMapper.readValue(jsonResponse, AppUserResponseDto.class);

        assertThat(result.email()).isEqualTo(appUserWithRoleDto.email());
        assertThat(result.id()).isNotNull();
        assertThat(result.portfolioItems()).isNull();
    }

    @Test
    @Transactional
    void createUserWithRole_withEmptyFields_returnsValidationErrors() throws Exception {
        // given
        var adminEmail = "admin_" + UUID.randomUUID() + "@example.com";
        var adminToken = generateToken(adminEmail, "adminPass", Role.ROLE_ADMIN);
        var appUserWithRoleDto = new AppUserWithRoleDto("", "", null);

        // when
        var jsonResponse = mockMvc.perform(post("/api/appuser/withrole")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appUserWithRoleDto)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        var result = objectMapper.readValue(jsonResponse, new TypeReference<Map<String, String>>() {});
        assertThat(result).containsEntry("password", "must not be empty");
        assertThat(result).containsEntry("role", "must not be null");
        assertThat(result).containsEntry("email", "must not be empty");
    }

    @Test
    @Transactional
    void createUserWithRole_withInvalidEmailAndEmptyPassword_returnsValidationErrors() throws Exception {
        // given
        var adminEmail = "admin_" + UUID.randomUUID() + "@example.com";
        var adminToken = generateToken(adminEmail, "adminPass", Role.ROLE_ADMIN);
        var appUserWithRoleDto = new AppUserWithRoleDto("notemail", "", Role.ROLE_ADMIN);

        // when
        var jsonResponse = mockMvc.perform(post("/api/appuser/withrole")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appUserWithRoleDto)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        var result = objectMapper.readValue(jsonResponse, new TypeReference<Map<String, String>>() {});
        assertThat(result).containsEntry("password", "must not be empty");
        assertThat(result).containsEntry("email", "must be a well-formed email address");
    }

    @Test
    @Transactional
    void findAllUsers_givenAdminToken_returnsAppUserResponseDtos() throws Exception {
        // given
        var adminEmail = "testuser_" + UUID.randomUUID() + "@example.com";
        var adminToken = generateToken(adminEmail, "adminPass", Role.ROLE_ADMIN);

        var userEmail1 = "testuser_" + UUID.randomUUID() + "@example.com";
        var userDto1 = new AppUserWithRoleDto(userEmail1, "userPass", Role.ROLE_USER);
        appUserService.createWithRole(userDto1);

        var userEmail2 = "testuser_" + UUID.randomUUID() + "@example.com";
        var userDto2 = new AppUserWithRoleDto(userEmail2, "userPass", Role.ROLE_USER);
        appUserService.createWithRole(userDto2);

        var userEmail3 = "testuser_" + UUID.randomUUID() + "@example.com";
        var userDto3 = new AppUserWithRoleDto(userEmail3, "userPass", Role.ROLE_USER);
        appUserService.createWithRole(userDto3);

        // when
        var jsonResponse = mockMvc.perform(get("/api/appuser")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        var result = objectMapper.readValue(jsonResponse, new TypeReference<List<AppUserResponseDto>>() {});

        assertThat(result).isNotEmpty();
        assertThat(result.size()).isGreaterThan(4);

        var userEmails = result
                .stream()
                .map(AppUserResponseDto::email)
                .toList();
        assertThat(userEmails).contains(adminEmail, userEmail1, userEmail2, userEmail3);

        for (var user : result) {
            assertThat(user.id()).isNotNull();
            assertThat(user.email()).isNotNull();
            assertThat(user.portfolioItems()).isNullOrEmpty();
        }
    }

    @Test
    @Transactional
    void findUserById_givenAdminTokenAndUserId_returnsResponseDto() throws Exception {
        // given
        var adminEmail = "testuser_" + UUID.randomUUID() + "@example.com";
        var adminToken = generateToken(adminEmail, "adminPass", Role.ROLE_ADMIN);

        var userEmail = "testuser_" + UUID.randomUUID() + "@example.com";
        var userDto = new AppUserWithRoleDto(userEmail, "userPass", Role.ROLE_USER);
        var userResponse = appUserService.createWithRole(userDto);

        // when
        var jsonResponse = mockMvc.perform(get("/api/appuser/" + userResponse.id())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        var result = objectMapper.readValue(jsonResponse, AppUserResponseDto.class);

        assertThat(result.email()).isEqualTo(userDto.email());
        assertThat(result.id()).isEqualTo(userResponse.id());
        assertThat(result.portfolioItems()).isNull();
    }

    @Test
    @Transactional
    void updateUser_givenUserTokenAndIdAndAppUserDto_returnsSavedResponseDto() throws Exception {
        // given
        var userEmail = "testuser_" + UUID.randomUUID() + "@example.com";
        var userDto = new AppUserWithRoleDto(userEmail, "userPass", Role.ROLE_USER);
        var userResponse = appUserService.createWithRole(userDto);
        var userToken = generateToken(userEmail, "userPass", Role.ROLE_USER);

        var updatedEmail = "updated_" + UUID.randomUUID() + "@example.com";
        var appUserDto = new AppUserDto(updatedEmail, "newpassword");

        // when
        var jsonResponse = mockMvc.perform(put("/api/appuser/" + userResponse.id())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appUserDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        var result = objectMapper.readValue(jsonResponse, AppUserResponseDto.class);

        assertThat(result.email()).isEqualTo(appUserDto.email());
        assertThat(result.id()).isEqualTo(userResponse.id());
        assertThat(result.portfolioItems()).isNullOrEmpty();
    }

    @Test
    @Transactional
    void deleteUser_givenUserTokenAndUserId_deletesAppUser() throws Exception {
        // given
        var userEmail = "testuser_" + UUID.randomUUID() + "@example.com";
        var userDto = new AppUserWithRoleDto(userEmail, "userPass", Role.ROLE_USER);
        var userResponse = appUserService.createWithRole(userDto);
        var userToken = generateToken(userEmail, "userPass", Role.ROLE_USER);

        // when
        mockMvc.perform(delete("/api/appuser/" + userResponse.id())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNoContent());

        // then
        try {
            appUserService.findById(userResponse.id());
            fail("Expected an EntityNotFoundException to be thrown");
        } catch (EntityNotFoundException e) {
            assertThat(e.getMessage()).isEqualTo("User not found");
        }
    }

    private String generateToken(String email, String password, Role role) {
        if (appUserRepository.findByEmail(email).isEmpty()) {
            appUserService.createWithRole(new AppUserWithRoleDto(email, password, role));
        }
        var userDetails = appUserDetailsService.loadUserByUsername(email);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
        return tokenService.generateToken(authentication);
    }
}
