package com.prax.crypto.config;

import com.prax.crypto.account.AppUser;
import com.prax.crypto.account.AppUserRepository;
import com.prax.crypto.account.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner init() {
        return args -> {
            if (appUserRepository.findByEmail("user@crypto.com").isEmpty()) {
                var user = AppUser.builder()
                        .email("user@crypto.com")
                        .password(passwordEncoder.encode("appPass"))
                        .role(Role.USER)
                        .build();
                appUserRepository.save(user);
            }
            if (appUserRepository.findByEmail("user2@crypto.com").isEmpty()) {
                var user2 = AppUser.builder()
                        .email("user2@crypto.com")
                        .password(passwordEncoder.encode("appPass"))
                        .role(Role.USER)
                        .build();
                appUserRepository.save(user2);
            }
            if (appUserRepository.findByEmail("admin@crypto.com").isEmpty()) {
                var admin = AppUser.builder()
                        .email("admin@crypto.com")
                        .password(passwordEncoder.encode("appPass"))
                        .role(Role.ADMIN)
                        .build();
                appUserRepository.save(admin);
            }
        };
    }
}
