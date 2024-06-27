package com.prax.crypto.account;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppUserService {

    private final AppUserRepository appUserRepository;

    public AppUserService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public AppUser createAppUser(AppUser user) {
        return appUserRepository.save(user);
    }

    public List<AppUser> findAll() {
        return appUserRepository.findAll();
    }

    public AppUser findById(Integer id) {
        return appUserRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    public AppUser updateAppUser(Integer id, AppUser user) {
        appUserRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return appUserRepository.save(user);
    }

    public void deleteAppUser(Integer id) {
        appUserRepository.deleteById(id);
    }
}
