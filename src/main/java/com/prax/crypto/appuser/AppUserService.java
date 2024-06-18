package com.prax.crypto.appuser;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppUserService {

    private final AppUserRepository repository;

    public AppUserService(AppUserRepository repository) {
        this.repository = repository;
    }

    public AppUser createAppUser(AppUser user) {
        return this.repository.save(user);
    }

    public List<AppUser> findAll() {
        return this.repository.findAll();
    }

    public AppUser findById(Integer id) {
        return this.repository.findById(id).orElse(null);
    }

    public AppUser updateAppUser(Integer id, AppUser user) {
        var existingUser = this.repository.findById(id).orElse(null);
        if (existingUser == null) {
            return null;
        }
        user.setId(id);
        return this.repository.save(user);
    }

    public void deleteAppUser(Integer id) {
        this.repository.deleteById(id);
    }
}
