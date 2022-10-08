package com.example.mapsgeo.controller;

import com.example.mapsgeo.document.User;
import com.example.mapsgeo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
public class UserController {
    private UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/user/{id}")
    public Mono<User> getUser(@PathVariable String id) {
        return service.findById(id);
    }

    @PostMapping("/user")
    public Mono<User> saveOrUpdateUser(@RequestBody User user) {
        return service.save(user);
    }
}
