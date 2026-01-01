package com.propel.controller;
import com.propel.dto.request.*;
import com.propel.dto.response.AuthResponse;
import com.propel.entity.User;
import com.propel.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/auth") @RequiredArgsConstructor
public class AuthController {
    private final AuthService service;
    @PostMapping("/register") public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest req) { return ResponseEntity.ok(service.register(req)); }
    @PostMapping("/login") public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest req) { return ResponseEntity.ok(service.login(req)); }
    @GetMapping("/me") public ResponseEntity<User> getMe(@AuthenticationPrincipal User user) { return ResponseEntity.ok(user); }
}
