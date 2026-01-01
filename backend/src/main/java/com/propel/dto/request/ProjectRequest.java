package com.propel.dto.request;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;
@Data public class ProjectRequest {
    private String title; private String description; private Double budget;
    private LocalDate deadline; private List<String> requiredSkills;
    private List<ChecklistItemRequest> checklist;
    @Data public static class ChecklistItemRequest { private String text; }
}
