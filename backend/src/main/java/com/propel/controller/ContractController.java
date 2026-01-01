package com.propel.controller;
import com.propel.entity.*;
import com.propel.service.ContractService;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/contracts") @RequiredArgsConstructor
public class ContractController {
    private final ContractService service;
    @PostMapping public ResponseEntity<Contract> create(@RequestBody CreateContractRequest req, @AuthenticationPrincipal User user) { return ResponseEntity.ok(service.createContract(req.getBidId(), user)); }
    @GetMapping("/my-contracts") public ResponseEntity<List<Contract>> getMine(@AuthenticationPrincipal User user) { return ResponseEntity.ok(service.getMyContracts(user)); }
    @PostMapping("/deliver") public ResponseEntity<Contract> deliver(@RequestBody DeliverWorkRequest req, @AuthenticationPrincipal User user) { return ResponseEntity.ok(service.deliverWork(req.getProjectId(), req.getWorkLink(), req.getNotes(), user)); }

    @Data public static class CreateContractRequest { private Long bidId; }
    @Data public static class DeliverWorkRequest { private Long projectId; private String workLink; private String notes; }
}
