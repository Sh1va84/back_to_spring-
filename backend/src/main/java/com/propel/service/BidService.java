package com.propel.service;
import com.propel.dto.request.BidRequest;
import com.propel.entity.*;
import com.propel.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service @RequiredArgsConstructor
public class BidService {
    private final BidRepository bidRepository;
    private final ProjectRepository projectRepository;

    public Bid placeBid(Long projectId, BidRequest request, User contractor) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new RuntimeException("Project not found"));
        Bid bid = Bid.builder().project(project).contractor(contractor)
                .bidAmount(request.getBidAmount()).daysToComplete(request.getDaysToComplete())
                .proposal(request.getProposal()).status(Bid.Status.PENDING).build();
        return bidRepository.save(bid);
    }
    public List<Bid> getBidsForProject(Long projectId) { return bidRepository.findByProjectId(projectId); }
}
