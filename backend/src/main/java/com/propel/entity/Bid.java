package com.propel.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bids")
public class Bid {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne @JoinColumn(name = "projectid")
    private Project project;
    @ManyToOne @JoinColumn(name = "contractorid")
    private User contractor;
    private Double bidAmount;
    private Integer daysToComplete;
    @Column(columnDefinition = "TEXT")
    private String proposal;
    @Enumerated(EnumType.STRING)
    private Status status;

    @PrePersist protected void onCreate() { if (status == null) status = Status.PENDING; }
    public enum Status { PENDING, ACCEPTED, REJECTED }
}
