package com.propel.entity;
import jakarta.persistence.*;
import lombok.Data;

@Data @Entity
public class ChecklistItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;
    private Boolean isCompleted = false;
}
