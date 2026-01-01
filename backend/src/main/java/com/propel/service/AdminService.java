package com.propel.service;
import com.propel.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service @RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final DisputeRepository disputeRepository;

    public Map<String, Long> getStats() {
        return Map.of(
            "totalUsers", userRepository.count(),
            "totalProjects", projectRepository.count(),
            "activeDisputes", (long) disputeRepository.findByStatus(com.propel.entity.Dispute.Status.OPEN).size()
        );
    }
}
