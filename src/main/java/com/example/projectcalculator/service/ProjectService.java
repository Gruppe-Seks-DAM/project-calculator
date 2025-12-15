package com.example.projectcalculator.service;

import com.example.projectcalculator.dto.ProjectDto;
import com.example.projectcalculator.model.Project;
import com.example.projectcalculator.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {
    private final ProjectRepository repository;

    public ProjectService(ProjectRepository repository) {
        this.repository = repository;
    }

    /**
     * Service-metoden som controlleren kalder.
     */
    public List<Project> getAllProjects() {
        return repository.findAllProjects();
    }

    public boolean create(ProjectDto dto) {
        Project p = new Project();
        p.setName(dto.getName());
        p.setDescription(dto.getDescription());
        p.setDeadline(dto.getDeadline());
        return repository.create(p);
    }

    public Optional<Project> findById(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public boolean updateProject(Project project) {
        // Check if project exists
        if (project.getId() == null || !repository.findById(project.getId()).isPresent()) {
            return false;
        }
        return repository.update(project);
    }

  public boolean delete(long id) {
        return repository.delete(id);
    }
}