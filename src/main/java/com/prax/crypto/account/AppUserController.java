package com.prax.crypto.account;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/appusers")
public class AppUserController {

    private final AppUserService appUserService;

    public AppUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @GetMapping
    public List<AppUser> findAll() {
        return appUserService.findAll();
    }

    @GetMapping("/{id}")
    public AppUser findById(@PathVariable Integer id) {
        return appUserService.findById(id);
    }

    @PostMapping
    public AppUser createAppUser(@RequestBody AppUser user) {
        return appUserService.createAppUser(user);
    }

    @PutMapping("/{id}")
    public AppUser updateAppUser(@PathVariable Integer id, @RequestBody AppUser user) {
        return this.appUserService.updateAppUser(id, user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteAppUser(@PathVariable Integer id) {
        this.appUserService.deleteAppUser(id);
    }

}
