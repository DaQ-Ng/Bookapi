package com.dat.bookapi;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class AuthController{
    private final UserService userService;

    public AuthController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/auth/register")
    public User register(@Valid @RequestBody User user){
        return userService.register(user);
    }

    @PostMapping("/auth/login")
    public User login(@Valid @RequestBody LoginRequest login){
        return userService.login(login);
    }
}