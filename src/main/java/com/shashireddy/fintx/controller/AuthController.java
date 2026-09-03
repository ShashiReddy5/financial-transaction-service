package com.shashireddy.fintx.controller;

import com.shashireddy.fintx.security.JwtService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Demo-only login endpoint: accepts a single hard-coded credential and, on
 * success, issues a JWT that the rest of the API accepts as a Bearer token.
 * A real deployment replaces this with a call to the org's identity
 * provider (Okta/OAuth2) - JwtService is what stays the same either way.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String DEMO_USER = "demo";
    private static final String DEMO_PASSWORD = "demo-password";

    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {
    }

    public record LoginResponse(String accessToken, String tokenType) {
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        if (!DEMO_USER.equals(request.username()) || !DEMO_PASSWORD.equals(request.password())) {
            return ResponseEntity.status(401).build();
        }

        String token = jwtService.issueToken(request.username());
        return ResponseEntity.ok(new LoginResponse(token, "Bearer"));
    }
}
