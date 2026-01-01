package com.propel.service;
import com.propel.entity.*;
import com.propel.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service @RequiredArgsConstructor
public class DisputeService {
    private final DisputeRepository disputeRepository;
    private final ContractRepository contractRepository;

    @Transactional
    public Dispute createDispute(Long contractId, String reason, String description, User user) {
        Contract contract = contractRepository.findById(contractId).orElseThrow(() -> new RuntimeException("Contract not found"));
        User defendant = contract.getClient().getId().equals(user.getId()) ? contract.getContractor() : contract.getClient();
        
        contract.setStatus(Contract.Status.DISPUTED);
        contractRepository.save(contract);
        
        Dispute dispute = Dispute.builder().contract(contract).raisedBy(user).defendant(defendant)
                .reason(reason).description(description).status(Dispute.Status.OPEN).build();
        return disputeRepository.save(dispute);
    }
    
    @Transactional
    public Dispute resolveDispute(Long id, String summary, String action) {
        Dispute dispute = disputeRepository.findById(id).orElseThrow(() -> new RuntimeException("Dispute not found"));
        dispute.setResolutionSummary(summary);
        dispute.setResolvedAt(LocalDateTime.now());
        
        Contract contract = dispute.getContract();
        if (action.equals("REFUND_AGENT")) {
            dispute.setStatus(Dispute.Status.RESOLVED_REFUND);
            contract.setEscrowStatus(Contract.EscrowStatus.REFUNDED);
        } else {
            dispute.setStatus(Dispute.Status.RESOLVED_PAYOUT);
            contract.setEscrowStatus(Contract.EscrowStatus.RELEASED);
        }
        contract.setStatus(Contract.Status.CANCELLED); // Or Terminated
        contractRepository.save(contract);
        return disputeRepository.save(dispute);
    }
}
