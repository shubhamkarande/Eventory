package com.eventory.controller;

import com.eventory.dto.AuthResponse;
import com.eventory.dto.LoginRequest;
import com.eventory.dto.RegisterRequest;
import com.eventory.model.User;
import com.eventory.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse.UserResponse> getCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = authService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(AuthResponse.UserResponse.fromUser(user));
    }

    @PutMapping("/interests")
    public ResponseEntity<AuthResponse.UserResponse> updateInterests(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody String interests) {
        User user = authService.updateInterests(userDetails.getUsername(), interests);
        return ResponseEntity.ok(AuthResponse.UserResponse.fromUser(user));
    }
}
