package com.example.projectcalculator.service;

// ProjectService.java
import com.example.projectcalculator.model.Project;
import com.example.projectcalculator.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Optional<Project> findById(Long id) {
        return projectRepository.findById(id);
    }

    @Transactional
    public boolean updateProject(Project project) {
        // Check if project exists
        if (project.getId() == null || !projectRepository.findById(project.getId()).isPresent()) {
            return false;
        }
        return projectRepository.update(project);
    }
}