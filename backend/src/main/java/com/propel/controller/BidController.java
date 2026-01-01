package com.propel.controller;
import com.propel.dto.request.BidRequest;
import com.propel.entity.*;
import com.propel.service.BidService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/bids") @RequiredArgsConstructor
public class BidController {
    private final BidService service;
    @PostMapping("/{projectId}") public ResponseEntity<Bid> bid(@PathVariable Long projectId, @RequestBody BidRequest req, @AuthenticationPrincipal User user) { return ResponseEntity.ok(service.placeBid(projectId, req, user)); }
    @GetMapping("/{projectId}") public ResponseEntity<List<Bid>> getBids(@PathVariable Long projectId) { return ResponseEntity.ok(service.getBidsForProject(projectId)); }
}
