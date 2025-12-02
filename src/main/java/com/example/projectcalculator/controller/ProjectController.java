package com.example.projectcalculator.controller;

import com.example.projectcalculator.dto.ProjectDto;
import com.example.projectcalculator.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectService service;

    public ProjectController(ProjectService service) {
        this.service = service;
    }
    
    @GetMapping("/projects")
    public String showProjects(Model model) {
        List<Project> projects = projectService.getAllProjects();
        model.addAttribute("projects", projects);
        return "projects"; // thymeleaf template: src/main/resources/templates/projects.html
  
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("projectDto", new ProjectDto());
        System.out.println("DEBUG: /projects/create handler hit");
        return "createProjectForm";
    }

    @PostMapping
    public String createProject(@Valid @ModelAttribute("projectDto")
                                    ProjectDto projectDto,
                                    BindingResult br) {

        if (br.hasErrors()) return "createProjectForm";

        service.create(projectDto);
        return "redirect:/projects";
    }
}
