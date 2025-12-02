package com.example.projectcalculator.service;

import com.example.projectcalculator.model.SubProject;
import com.example.projectcalculator.repository.SubProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class SubProjectService {

    private final SubProjectRepository subProjectRepository;

    @Autowired
    public SubProjectService(SubProjectRepository subProjectRepository) {
        this.subProjectRepository = subProjectRepository;
    }

    /**
     * #171 - Service: updateSubProject(SubProject)
     */
    @Transactional
    public boolean updateSubProject(SubProject subProject) {
        // Validate that the subproject exists and belongs to the specified project
        if (!subProjectRepository.existsByIdAndProjectId(
                subProject.getId(),
                subProject.getProjectId())) {
            return false;
        }

        // Validate name (basic validation)
        if (subProject.getName() == null || subProject.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Subproject name cannot be empty");
        }

        // Update the subproject
        int rowsUpdated = subProjectRepository.update(subProject);
        return rowsUpdated > 0;
    }

    public Optional<SubProject> getSubProjectById(Long id) {
        return subProjectRepository.findById(id);
    }
}