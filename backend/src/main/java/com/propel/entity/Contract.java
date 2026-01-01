package com.propel.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "contracts")
public class Contract {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne @JoinColumn(name = "project_id")
    private Project project;
    @ManyToOne @JoinColumn(name = "client_id")
    private User client;
    @ManyToOne @JoinColumn(name = "contractor_id")
    private User contractor;
    @OneToOne @JoinColumn(name = "bid_id")
    private Bid bid;
    private Double amount;
    private Integer days;
    private String workLink;
    @Column(columnDefinition = "TEXT")
    private String workNotes;
    private LocalDateTime submittedAt;
    @Enumerated(EnumType.STRING)
    private Status status;
    @Enumerated(EnumType.STRING)
    private EscrowStatus escrowStatus;

    @PrePersist protected void onCreate() { 
        if (status == null) status = Status.ACTIVE;
        if (escrowStatus == null) escrowStatus = EscrowStatus.HELD;
    }
    public enum Status { ACTIVE, WORK_SUBMITTED, COMPLETED, DISPUTED, CANCELLED }
    public enum EscrowStatus { HELD, RELEASED, REFUNDED }
}
