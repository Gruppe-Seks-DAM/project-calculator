package com.example.projectcalculator.controller;

import com.example.projectcalculator.dto.SubProjectDto;
import com.example.projectcalculator.service.SubProjectService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/projects/{projectId}/subprojects")
public class SubProjectController {

    private final SubProjectService service;

    public SubProjectController(SubProjectService service) {
        this.service = service;
    }

    @GetMapping
    public String listSubProjects(@PathVariable long projectId, Model model) {
        model.addAttribute("subprojects", service.getAllSubProjects());
        model.addAttribute("projectId", projectId);
        return "subprojects";
    }

    @GetMapping("/create")
    public String showCreateForm(@PathVariable long projectId, Model model) {
        model.addAttribute("subProjectDto", new SubProjectDto());
        model.addAttribute("projectId", projectId);
        return "createSubProjectForm";
    }

    @PostMapping
    public String createSubProject(@PathVariable long projectId,
                                   @Valid @ModelAttribute("subProjectDto") SubProjectDto subProjectDto,
                                   BindingResult br) {
        if (br.hasErrors()) {
            return "createSubProjectForm";
        }

        boolean created = service.create(subProjectDto, projectId);
        if (!created) {
            return "redirect:/projects/" + projectId + "/subprojects?error=Could not create subproject";
        }

        return "redirect:/projects/" + projectId + "/subprojects?success=Subproject created";
    }

    @PostMapping("/{id}/delete")
    public String deleteSubProject(@PathVariable long projectId, @PathVariable long id) {
        boolean deleted = service.delete(id);
        if (!deleted) {
            return "redirect:/projects/" + projectId + "/subprojects?error=Could not delete subproject";
        }
        return "redirect:/projects/" + projectId + "/subprojects?success=Subproject deleted";
    }
}
