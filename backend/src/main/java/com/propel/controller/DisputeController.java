package com.propel.controller;
import com.propel.entity.*;
import com.propel.service.DisputeService;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/disputes") @RequiredArgsConstructor
public class DisputeController {
    private final DisputeService service;
    @PostMapping public ResponseEntity<Dispute> create(@RequestBody CreateDisputeRequest req, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(service.createDispute(req.contractId, req.reason, req.description, user));
    }
    @PutMapping("/{id}/resolve") public ResponseEntity<Dispute> resolve(@PathVariable Long id, @RequestBody ResolveDisputeRequest req) {
        return ResponseEntity.ok(service.resolveDispute(id, req.resolutionSummary, req.action));
    }
    @Data public static class CreateDisputeRequest { Long contractId; String reason; String description; }
    @Data public static class ResolveDisputeRequest { String resolutionSummary; String action; }
}
