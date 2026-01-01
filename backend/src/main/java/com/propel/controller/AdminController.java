package com.propel.controller;
import com.propel.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController @RequestMapping("/api/admin") @RequiredArgsConstructor
public class AdminController {
    private final AdminService service;
    @GetMapping("/stats") public ResponseEntity<Map<String, Long>> getStats() { return ResponseEntity.ok(service.getStats()); }
}
