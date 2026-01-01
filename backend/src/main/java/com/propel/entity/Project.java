package com.propel.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "projects")
public class Project {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;
    private Double budget;
    private LocalDate deadline;
    @ElementCollection
    private List<String> requiredSkills;
    @Enumerated(EnumType.STRING)
    private Status status;
    @ManyToOne @JoinColumn(name = "created_byid")
    private User createdBy;
    private String workSubmissionLink;
    @OneToMany(cascade = CascadeType.ALL)
    private List<ChecklistItem> checklist;

    @PrePersist protected void onCreate() { if (status == null) status = Status.OPEN; }
    public enum Status { OPEN, IN_PROGRESS, WORK_SUBMITTED, COMPLETED, CANCELLED }
}
