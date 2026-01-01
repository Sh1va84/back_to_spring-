package com.propel.service;
import com.propel.dto.request.*;
import com.propel.dto.response.*;
import com.propel.entity.User;
import com.propel.repository.UserRepository;
import com.propel.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class AuthService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        var user = User.builder().name(request.getName()).email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.valueOf(request.getRole())).build();
        var saved = repository.save(user);
        return AuthResponse.builder().token(jwtService.generateToken(user))._id(saved.getId()).name(saved.getName()).email(saved.getEmail()).role(saved.getRole().name()).build();
    }
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        var user = repository.findByEmail(request.getEmail()).orElseThrow();
        return AuthResponse.builder().token(jwtService.generateToken(user))._id(user.getId()).name(user.getName()).email(user.getEmail()).role(user.getRole().name()).build();
    }
}
