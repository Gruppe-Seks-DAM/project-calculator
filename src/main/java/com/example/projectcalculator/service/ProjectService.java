package com.example.projectcalculator.service;

import com.example.projectcalculator.dto.ProjectDto;
import com.example.projectcalculator.model.Project;
import com.example.projectcalculator.repository.ProjectRepository;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {
    private final ProjectRepository repo;

    public ProjectService(ProjectRepository repo) {
        this.repo = repo;
    }

    public long create(ProjectDto dto) {
        Project p = new Project();
        p.setName(dto.getName());
        p.setDescription(dto.getDescription());
        p.setDeadline(dto.getDeadline());
        return repo.create(p);
    }
}