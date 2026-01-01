package com.propel.service;
import com.propel.entity.*;
import com.propel.repository.*;
import com.propel.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service @RequiredArgsConstructor
public class ContractService {
    private final ContractRepository contractRepository;
    private final BidRepository bidRepository;
    private final ProjectRepository projectRepository;
    private final InvoiceGenerator invoiceGenerator;
    private final EmailService emailService;

    @Transactional
    public Contract createContract(Long bidId, User client) {
        Bid bid = bidRepository.findById(bidId).orElseThrow(() -> new RuntimeException("Bid not found"));
        if (!bid.getProject().getCreatedBy().getId().equals(client.getId())) throw new RuntimeException("Not authorized");
        Contract contract = Contract.builder().project(bid.getProject()).client(client).contractor(bid.getContractor())
                .bid(bid).amount(bid.getBidAmount()).days(bid.getDaysToComplete())
                .status(Contract.Status.ACTIVE).escrowStatus(Contract.EscrowStatus.HELD).build();
        bid.getProject().setStatus(Project.Status.IN_PROGRESS);
        projectRepository.save(bid.getProject());
        bid.setStatus(Bid.Status.ACCEPTED);
        bidRepository.save(bid);
        return contractRepository.save(contract);
    }
    public List<Contract> getMyContracts(User user) { return contractRepository.findByClientIdOrContractorId(user.getId(), user.getId()); }
    @Transactional
    public Contract deliverWork(Long projectId, String workLink, String notes, User contractor) {
        Contract contract = contractRepository.findByProjectIdAndContractorIdAndStatus(projectId, contractor.getId(), Contract.Status.ACTIVE)
                .orElseThrow(() -> new RuntimeException("No active contract"));
        contract.setWorkLink(workLink); contract.setWorkNotes(notes);
        contract.setSubmittedAt(LocalDateTime.now()); contract.setStatus(Contract.Status.COMPLETED);
        contract.setEscrowStatus(Contract.EscrowStatus.RELEASED);
        contract.getProject().setStatus(Project.Status.COMPLETED);
        projectRepository.save(contract.getProject());
        
        String invoice = invoiceGenerator.generateInvoiceBase64(contract, contractor, contract.getClient());
        emailService.sendEmailWithAttachment(contract.getClient().getEmail(), "Work Delivered", "Invoice Attached", "Invoice.html", invoice);
        return contractRepository.save(contract);
    }
}
