package com.prax.crypto.account;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AppUserService {

    private final AppUserRepository appUserRepository;

    public AppUser create(AppUser user) {
        return appUserRepository.save(user);
    }

    public List<AppUser> findAll() {
        return appUserRepository.findAll();
    }

    public AppUser findById(Integer id) {
        return appUserRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    public AppUser update(Integer id, AppUser user) {
        appUserRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return appUserRepository.save(user);
    }

    public void delete(Integer id) {
        appUserRepository.deleteById(id);
    }
}
