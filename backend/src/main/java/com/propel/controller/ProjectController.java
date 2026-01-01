package com.propel.controller;
import com.propel.dto.request.ProjectRequest;
import com.propel.entity.*;
import com.propel.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/projects") @RequiredArgsConstructor
public class ProjectController {
    private final ProjectService service;
    @PostMapping public ResponseEntity<Project> create(@RequestBody ProjectRequest req, @AuthenticationPrincipal User user) { return ResponseEntity.ok(service.createProject(req, user)); }
    @GetMapping public ResponseEntity<List<Project>> getAll() { return ResponseEntity.ok(service.getAllProjects()); }
    @GetMapping("/{id}") public ResponseEntity<Project> getById(@PathVariable Long id) { return ResponseEntity.ok(service.getProjectById(id)); }
    @DeleteMapping("/{id}") public ResponseEntity<?> delete(@PathVariable Long id, @AuthenticationPrincipal User user) { service.deleteProject(id, user); return ResponseEntity.ok().build(); }
}
