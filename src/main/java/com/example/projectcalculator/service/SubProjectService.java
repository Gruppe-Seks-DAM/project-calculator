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

    public List<SubProject> getAllSubProjects(long projectId) {
        return subprojectRepository.listAllSubProjectsByProjectId(projectId);
    }

    public SubProject getSubProjectById(long projectId, long id) {
        return subprojectRepository.findSubProjectById(projectId, id);
    }

    public boolean createSubProject(SubProject subproject) {
        return subprojectRepository.createSubProject(subproject);
    }

    public boolean updateSubProject(SubProject subproject) {
        return subprojectRepository.updateSubProject(subproject);
    }

    public boolean deleteSubProject(long projectId, long id) {
        return subprojectRepository.deleteSubProject(projectId, id);
    }
}
