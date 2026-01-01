package com.propel.service;
import com.propel.dto.request.ProjectRequest;
import com.propel.entity.*;
import com.propel.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;
import java.util.List;

@Service @RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    public Project createProject(ProjectRequest request, User user) {
        List<ChecklistItem> checklist = request.getChecklist().stream().map(i -> {
            ChecklistItem c = new ChecklistItem(); c.setText(i.getText()); return c;
        }).collect(Collectors.toList());
        Project project = Project.builder().title(request.getTitle()).description(request.getDescription())
                .budget(request.getBudget()).deadline(request.getDeadline()).requiredSkills(request.getRequiredSkills())
                .checklist(checklist).createdBy(user).status(Project.Status.OPEN).build();
        return projectRepository.save(project);
    }
    public List<Project> getAllProjects() { return projectRepository.findAllByOrderByIdDesc(); }
    public Project getProjectById(Long id) { return projectRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found")); }
    public void deleteProject(Long id, User user) {
        Project project = getProjectById(id);
        if (!project.getCreatedBy().getId().equals(user.getId())) throw new RuntimeException("Unauthorized");
        projectRepository.delete(project);
    }
}
