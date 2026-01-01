package com.propel.repository;
import com.propel.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findAllByOrderByIdDesc();
}
