package com.propel.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "disputes")
public class Dispute {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne @JoinColumn(name = "contractid")
    private Contract contract;
    @ManyToOne @JoinColumn(name = "raised_byid")
    private User raisedBy;
    @ManyToOne @JoinColumn(name = "defendantid")
    private User defendant;
    private String reason;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Enumerated(EnumType.STRING)
    private Status status;
    private String resolutionSummary;
    private LocalDateTime resolvedAt;

    @PrePersist protected void onCreate() { if (status == null) status = Status.OPEN; }
    public enum Status { OPEN, UNDER_REVIEW, RESOLVED_REFUND, RESOLVED_PAYOUT }
}
