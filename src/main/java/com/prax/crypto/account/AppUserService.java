package com.prax.crypto.account;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppUserService {

    private final AppUserRepository appUserRepository;

    public AppUserService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public AppUser createAppUser(AppUser user) {
        return this.appUserRepository.save(user);
    }

    public List<AppUser> findAll() {
        return this.appUserRepository.findAll();
    }

    public AppUser findById(Integer id) {
        return this.appUserRepository.findById(id).orElse(null);
    }

    public AppUser updateAppUser(Integer id, AppUser user) {
        var existingUser = this.appUserRepository.findById(id).orElse(null);
        if (existingUser == null) {
            return null;
        }
        return this.appUserRepository.save(user);
    }

    public void deleteAppUser(Integer id) {
        this.appUserRepository.deleteById(id);
    }
}
