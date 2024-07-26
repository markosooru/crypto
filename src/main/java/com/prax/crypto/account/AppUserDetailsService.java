package com.prax.crypto.account;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        return appUserRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
    }

    // generate getAuthorities method
    public List<SimpleGrantedAuthority> getAuthorities(String email) {
        return appUserRepository.findByEmail(email)
                .map(AppUser::getRole)
                .map(role -> List.of(new SimpleGrantedAuthority(role.name())))
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
    }
}