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
import java.util.Optional;

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


    // GET /projects/{id}/edit - Show edit form pre-filled with project data
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Project> project = service.findById(id);
        if (project.isEmpty()) {
            return "redirect:/projects?error=Project not found";
        }
        model.addAttribute("project", project.get());
        return "edit";
    }

    // POST /projects/{id}/edit - Handle form submission
    @PostMapping("/{id}/edit")
    public String updateProject(@PathVariable Long id,
                                @Valid @ModelAttribute("project") Project project,
                                BindingResult bindingResult,
                                Model model) {

        // Validate project ID matches path
        if (!id.equals(project.getId())) {
            return "redirect:/projects?error=Invalid project ID";
        }

        // Check for validation errors
        if (bindingResult.hasErrors()) {
            return "edit";
        }

        // Update project
        boolean updated = service.updateProject(project);
        if (!updated) {
            model.addAttribute("error", "Failed to update project");
            return "edit";
        }

        // Redirect to projects list after successful update
        return "redirect:/projects?success=Project updated successfully";
    }

    @PostMapping("/{id}/delete")
    public String deleteProject(@PathVariable long id) {
        boolean deleted = service.delete(id);

        if (!deleted) {
            return "redirect:/projects?error=Could not delete project";
        }
        return "redirect:/projects?success=Project deleted successfully";
    }
}