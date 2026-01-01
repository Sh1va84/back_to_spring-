package com.propel.dto.response;
import lombok.*;
@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class AuthResponse { private String token; private Long id; private String name; private String email; private String role; }
