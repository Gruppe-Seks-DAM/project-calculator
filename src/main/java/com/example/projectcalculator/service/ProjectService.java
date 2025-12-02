package com.example.projectcalculator.service;

import com.example.projectcalculator.dto.ProjectDto;
import com.example.projectcalculator.model.Project;
import com.example.projectcalculator.repository.ProjectRepository;
import org.springframework.stereotype.Service
import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    /**
     * Service-metoden som controlleren kalder.
     */
    public List<Project> getAllProjects() {
        return projectRepository.findAllProjects();
    }

    public long create(ProjectDto dto) {
        Project p = new Project();
        p.setName(dto.getName());
        p.setDescription(dto.getDescription());
        p.setDeadline(dto.getDeadline());
        return repo.create(p);
    }
}

