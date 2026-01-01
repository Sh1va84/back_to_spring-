package com.propel.repository;
import com.propel.entity.Dispute;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface DisputeRepository extends JpaRepository<Dispute, Long> {
    List<Dispute> findByStatus(Dispute.Status status);
}
