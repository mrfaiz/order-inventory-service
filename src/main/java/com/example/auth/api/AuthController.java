package com.example.auth.api;

import com.example.auth.dto.LoginRequest;
import com.example.auth.dto.TokenResponse;
import com.example.inventory.service.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequest request) {
        // DEMO: hardcoded user
        if (!"admin".equals(request.username()) || !"password".equals(request.password())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        return new TokenResponse(jwtService.generateToken(request.username()));
    }
}