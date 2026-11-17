package com.dat.bookapi.auth;

import com.dat.bookapi.user.User;
import com.dat.bookapi.user.UserService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import java.security.Principal;

@RestController
public class AuthController{
    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(UserService userService, JwtService jwtService){
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/auth/register")
    public User register(@Valid @RequestBody User user){
        return userService.register(user);
    }

    @PostMapping("/auth/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest login){
        User user = userService.login(login);
        String token = jwtService.generateToken(user.getEmail());
        return new LoginResponse(token);
    }

    // not in the permitAll list → needs a token; returns {id, name} (email + password are WRITE_ONLY)
    @GetMapping("/auth/me")
    public User me(Principal principal){
        return userService.getByEmail(principal.getName());
    }
}