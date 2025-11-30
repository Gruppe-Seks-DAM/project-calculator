package com.example.projectcalculator.service;

import com.example.projectcalculator.repository.ProjectRepository;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    private final ProjectRepository repo;

    public ProjectService(ProjectRepository repo) {
        this.repo = repo;
    }

    public boolean delete(long id) {
        return repo.delete(id);
    }
}