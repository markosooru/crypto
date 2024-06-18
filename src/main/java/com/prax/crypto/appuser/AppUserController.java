package com.prax.crypto.appuser;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/appusers")
public class AppUserController {

    private final AppUserService service;

    public AppUserController(AppUserService service) {
        this.service = service;
    }

    @GetMapping
    public List<AppUser> findAll() {
        return this.service.findAll();
    }

    @GetMapping("/{id}")
    public AppUser findById(@PathVariable Integer id) {
        return this.service.findById(id);
    }

    @PostMapping
    public AppUser createAppUser(@RequestBody AppUser user) {
        return this.service.createAppUser(user);
    }

    @PutMapping("/{id}")
    public AppUser updateAppUser(@PathVariable Integer id, @RequestBody AppUser user) {
        return this.service.updateAppUser(id, user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteAppUser(@PathVariable Integer id) {
        this.service.deleteAppUser(id);
    }

}
