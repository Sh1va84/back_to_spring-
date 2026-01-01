package com.propel.repository;
import com.propel.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface ContractRepository extends JpaRepository<Contract, Long> {
    List<Contract> findByClientIdOrContractorId(Long clientId, Long contractorId);
    Optional<Contract> findByProjectIdAndContractorIdAndStatus(Long projectId, Long contractorId, Contract.Status status);
}
