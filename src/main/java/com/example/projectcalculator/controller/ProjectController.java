package com.example.projectcalculator.controller;

import com.example.projectcalculator.dto.ProjectDto;
import com.example.projectcalculator.model.Project;
import com.example.projectcalculator.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService service;

    public ProjectController(ProjectService service) {
        this.service = service;
    }

    @GetMapping
    public String showProjects(Model model) {
        List<Project> projects = service.getAllProjects();
        model.addAttribute("projects", projects);
        return "projects"; // thymeleaf template: src/main/resources/templates/projects.html
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("projectDto", new ProjectDto());
        return "createProjectForm";
    }

    @PostMapping
    public String createProject(
            @Valid @ModelAttribute("projectDto")
            ProjectDto projectDto,
            BindingResult br) {

        if (br.hasErrors()) return "createProjectForm";

        service.create(projectDto);
        return "redirect:/projects";
    }

    @PostMapping("/{id}/delete")
    public String deleteProject(@PathVariable long id) {
        boolean deleted = service.delete(id);

        if (!deleted) {
            return "redirect:/projects?error=Could not delete project";
        }
        return "redirect:/projects?success=Project deleted successfully";
    }

    @GetMapping("/debug/list")
    @ResponseBody
    public List<Project> debugList() {
        return service.getAllProjects(); // eller repo.findAllProjects via service
    }
}
