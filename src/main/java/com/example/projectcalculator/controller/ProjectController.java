package com.example.projectcalculator.controller;
import com.example.projectcalculator.model.Project;
import com.example.projectcalculator.service.ProjectService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/projects")
public class ProjectController {

    ///  DEPENDENCY INJECTION OF THE PROJECT SERVICE
    private final ProjectService service;

    public ProjectController(ProjectService service) {
        this.service = service;
    }
    ///  LISTS ALL PROJECTS
    @GetMapping
    public String showProjects(Model model) {
        List<Project> projects = service.getAllProjects();
        model.addAttribute("projects", projects);
        return "projects";
    }
    ///  SHOW FORM TO CREATE A NEW PROJECT
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("project", new Project());
        return "createProjectForm";
    }
    ///  CREATE A NEW PROJECT AND REDIRECT TO /projects WITH SUCCESS OR ERROR MESSAGE
    @PostMapping("/create")
    public String createProject(@ModelAttribute("project") Project project) {
        boolean created = service.createProject(project);
        if (!created) {
            return "redirect:/projects?error=Could not create project";
        }
        return "redirect:/projects?success=Project created successfully";
    }
    ///  DELETE A PROJECT BY ID AND REDIRECT TO /projects WITH SUCCESS OR ERROR MESSAGE
    @PostMapping("/{id}/delete")
    public String deleteProject(@PathVariable long id) {
        boolean deleted = service.deleteProject(id);

        if (!deleted) {
            return "redirect:/projects?error=Could not delete project";
        }
        return "redirect:/projects?success=Project deleted successfully";
    }
}
