package com.example.projectcalculator.service;

import com.example.projectcalculator.model.SubProject;
import com.example.projectcalculator.repository.SubProjectRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SubProjectService {

    private final SubProjectRepository subprojectRepository;

    public SubProjectService(SubProjectRepository subprojectRepository) {
        this.subprojectRepository = subprojectRepository;
    }


    public List<SubProject> getAllSubProjects() {
        return subprojectRepository.findAllSubProjects();
    }

    /**
     * Create a SubProject under the given projectId.
     * Returns true on success.
     */
    public boolean create(SubProject subProject, long projectId) {
        return subprojectRepository.createSubProject(subProject, projectId);
    }

    public boolean delete(long id) {
        return subprojectRepository.delete(id);
    }
}
