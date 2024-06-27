package com.prax.crypto.account;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
        return this.appUserService.findAll();
    }

    @GetMapping("/{id}")
    public AppUser findById(@PathVariable Integer id) {
        return this.appUserService.findById(id);
    }

    @PostMapping
    public AppUser createAppUser(@RequestBody AppUser user) {
        return this.appUserService.createAppUser(user);
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
