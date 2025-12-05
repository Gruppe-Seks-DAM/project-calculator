package com.example.projectcalculator.controller;

import com.example.projectcalculator.model.Project;
import com.example.projectcalculator.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@Controller
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    // GET /projects/{id}/edit - Show edit form pre-filled with project data
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Project> project = projectService.findById(id);
        if (project.isEmpty()) {
            return "redirect:/projects?error=Project not found";
        }
        model.addAttribute("project", project.get());
        return "project/edit";
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
            return "project/edit";
        }

        // Update project
        boolean updated = projectService.updateProject(project);
        if (!updated) {
            model.addAttribute("error", "Failed to update project");
            return "project/edit";
        }

        // Redirect to projects list after successful update
        return "redirect:/projects?success=Project updated successfully";
    }
}