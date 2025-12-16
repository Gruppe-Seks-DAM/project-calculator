package com.example.projectcalculator.service;

import com.example.projectcalculator.dto.SubProjectDto;
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

    /**
     * Service method used by controller.
     */
    public List<SubProject> getAllSubProjects() {
        return subprojectRepository.findAllSubProjects();
    }

    /**
     * Get subprojects for a specific project.
     */
    public List<SubProject> getSubProjectsByProjectId(long projectId) {
        return subprojectRepository.findByProjectId(projectId);
    }

    /**
     * Create a SubProject under the given projectId.
     * Returns true on success.
     */
    public boolean create(SubProjectDto subProjectDto, long projectId) {
        SubProject p = new SubProject();
        p.setName(subProjectDto.getName());
        p.setDescription(subProjectDto.getDescription());
        p.setDeadline(subProjectDto.getDeadline());
        return subprojectRepository.createSubProject(p, projectId);
    }

    public boolean delete(long id) {
        return subprojectRepository.delete(id);
    }
}
