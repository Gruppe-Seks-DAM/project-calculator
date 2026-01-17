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
        return subprojectRepository.listAllSubProjects();
    }

    public SubProject getSubProjectById(long id) {
        return subprojectRepository.findSubProjectById(id);
    }

    public boolean createSubProject(SubProject subproject) {
        return subprojectRepository.createSubProject(subproject);
    }

    public boolean updateSubProject(SubProject subproject) {
        return subprojectRepository.updateSubProject(subproject);
    }

    public boolean deleteSubProject(long id) {
        return subprojectRepository.deleteSubProject(id);
    }
}
