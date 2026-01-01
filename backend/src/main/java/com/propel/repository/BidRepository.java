package com.propel.repository;
import com.propel.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface BidRepository extends JpaRepository<Bid, Long> {
    List<Bid> findByProjectId(Long projectId);
}
